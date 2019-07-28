/*
 * Copyright (C) 2018 Markus Ressel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.markusressel.k4ever.rest

import android.util.Log
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.coroutines.awaitObject
import com.github.kittinunf.fuel.rx.rxObject
import com.github.kittinunf.fuel.rx.rxResponsePair
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Markus on 08.02.2018.
 */
class RequestManager(hostname: String = "k4ever.freitagsrunde.org/api/v1", var basicAuthConfig: BasicAuthConfig? = null) {

    var hostname: String = hostname
        set(value) {
            field = value
            updateBaseUrl()
        }

    private val fuelManager = FuelManager()
    private var jwtToken: JwtTokenModel? = null

    init {
        addLogger()
        updateBaseUrl()
        setConfig()
    }

    /**
     * Adds loggers to Fuel requests
     */
    private fun addLogger() {
        fuelManager.addResponseInterceptor { next: (Request, Response) -> Response ->
            { req: Request, res: Response ->
                Log.v("Fuel-Request", req.toString())
                Log.v("Fuel-Response", res.toString())
                next(req, res)
            }
        }
    }

    /**
     * Updates the base URL in Fuel client according to configuration parameters
     */
    private fun updateBaseUrl() {
        if (hostname.startsWith("https://")) {
            fuelManager.basePath = hostname
        } else {
            fuelManager.basePath = "https://$hostname"
        }

    }

    private fun setConfig() {
        fuelManager.timeoutInMillisecond = TimeUnit.SECONDS.toMillis(2).toInt()
        fuelManager.timeoutReadInMillisecond = TimeUnit.SECONDS.toMillis(2).toInt()
    }

    /**
     * Creates an (authenticated) request
     *
     * @param url the url
     * @param urlParameters query parameters
     * @param method the request type (f.ex. GET)
     */
    private fun createRequest(url: String, urlParameters: List<Pair<String, Any?>> = emptyList(),
                              method: Method): Request {
        return getAuthenticatedRequest(fuelManager.request(method, url, urlParameters))
    }

    /**
     * Applies basic authentication parameters to a request
     */
    private fun getAuthenticatedRequest(request: Request): Request {
        if (!jwtIsValid()) {
            // TODO: switch to coroutines instead of rxjava
            runBlocking(Dispatchers.IO) {
                login("admin", "admin")
            }
        }

        val request = addBasicAuth(request)
        return addJwt(request)
    }

    private fun addJwt(request: Request): Request {
        // jwt headers
        return request.appendHeader(
                "Authorization" to "Bearer ${jwtToken!!.token}"
        )
    }

    private fun addBasicAuth(request: Request): Request {
        // TODO: basic auth is currently neither in use nor supported
//        basicAuthConfig?.let {
//            return request.authentication().basic(username = it.username, password = it.password)
//        }

        return request
    }

    /**
     * Sends a login request
     */
    private suspend fun login(username: String, password: String) {
        val jsonData = jsonObject(
                "name" to username,
                "password" to password
        )
        val json = Gson().toJson(jsonData)
        var request = fuelManager.request(Method.POST, "/login/")
        request = addBasicAuth(request)

        val deserializer = singleDeserializer<JwtTokenModel>()
        jwtToken = request.body(json)
                .header(HEADER_CONTENT_TYPE_JSON)
                .await(deserializer)
    }

    private fun jwtIsValid(): Boolean {
        jwtToken?.let {
            return Calendar.getInstance().time.before(it.expire)
        }

        return false
    }

    /**
     * Do a generic request
     *
     * @param url the URL
     * @param method the request type (f.ex. GET)
     */
    fun doRequest(url: String,
                  method: Method): Single<Pair<Response, ByteArray>> {
        return createRequest(url = url, method = method).rxResponsePair().subscribeOn(Schedulers.io())
    }

    /**
     * Do a simple request that expects a json response body
     *
     * @param url the URL
     * @param method the request type (f.ex. GET)
     * @param deserializer a deserializer for the response json body
     */
    suspend fun <T : Any> awaitRequest(url: String, method: Method,
                                       deserializer: ResponseDeserializable<T>): T {
        return createRequest(url = url, method = method).awaitObject(deserializer)
    }

    /**
     * Do a request with query parameters that expects a json response body
     *
     * @param url the URL
     * @param urlParameters url query parameters
     * @param method the request type (f.ex. GET)
     * @param deserializer a deserializer for the <b>response</b> json body
     */
    suspend fun <T : Any> awaitRequest(url: String, urlParameters: List<Pair<String, Any?>>, method: Method,
                                       deserializer: ResponseDeserializable<T>): T {
        return createRequest(url = url, urlParameters = urlParameters, method = method).awaitObject(deserializer)
    }

    /**
     * Do a request with a json body that expects a json response body
     *
     * @param url the URL
     * @param method the request type (f.ex. GET)
     * @param jsonData an Object that will be serialized to json
     * @param deserializer a deserializer for the <b>response</b> json body
     */
    fun <T : Any> doJsonRequest(url: String, method: Method, jsonData: Any,
                                deserializer: ResponseDeserializable<T>): Single<T> {
        val json = Gson().toJson(jsonData)
        return createRequest(url = url, method = method).body(json).header(HEADER_CONTENT_TYPE_JSON)
                .rxObject(deserializer).map {
                    it.component1() ?: throw it.component2() ?: throw Exception()
                }.subscribeOn(Schedulers.io())
    }

    /**
     * Do a request with a json body
     *
     * @param url the URL
     * @param method the request type (f.ex. GET)
     * @param jsonData an Object that will be serialized to json
     */
    fun doJsonRequest(url: String, method: Method,
                      jsonData: Any): Single<Pair<Response, ByteArray>> {
        val json = Gson().toJson(jsonData)
        return createRequest(url = url, method = method).body(json).header(HEADER_CONTENT_TYPE_JSON)
                .rxResponsePair().subscribeOn(Schedulers.io())
    }

    companion object {
        val HEADER_CONTENT_TYPE_JSON = "Content-Type" to "application/json"
    }

}