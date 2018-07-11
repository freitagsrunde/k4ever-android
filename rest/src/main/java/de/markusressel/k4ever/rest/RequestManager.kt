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
import com.github.kittinunf.fuel.rx.rx_object
import com.github.kittinunf.fuel.rx.rx_response
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.reactivex.Single

/**
 * Created by Markus on 08.02.2018.
 */
class RequestManager(hostname: String = "localhost", var basicAuthConfig: BasicAuthConfig? = null) {

    var hostname: String = hostname
        set(value) {
            field = value
            updateBaseUrl()
        }

    private val fuelManager = FuelManager()

    init {
        addLogger()
        updateBaseUrl()
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
        if (hostname.startsWith("http")) {
            fuelManager.basePath = hostname
        } else {
            fuelManager.basePath = "http://$hostname"
        }
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
        basicAuthConfig?.let {
            return request.authenticate(username = it.username, password = it.password)
        }

        return request
    }

    /**
     * Do a generic request
     *
     * @param url the URL
     * @param method the request type (f.ex. GET)
     */
    fun doRequest(url: String,
                  method: Method): Single<Pair<Response, Result<ByteArray, FuelError>>> {
        return createRequest(url = url, method = method).rx_response().map {
            it.second.component2()?.let {
                throw it
            }
            it
        }.map {
            it
        }
    }

    /**
     * Do a simple request that expects a json response body
     *
     * @param url the URL
     * @param method the request type (f.ex. GET)
     * @param deserializer a deserializer for the response json body
     */
    fun <T : Any> doRequest(url: String, method: Method,
                            deserializer: ResponseDeserializable<T>): Single<T> {
        return createRequest(url = url, method = method).rx_object(deserializer).map {
            it.component1() ?: throw it.component2() ?: throw Exception()
        }
    }

    /**
     * Do a request with query parameters that expects a json response body
     *
     * @param url the URL
     * @param urlParameters url query parameters
     * @param method the request type (f.ex. GET)
     * @param deserializer a deserializer for the <b>response</b> json body
     */
    fun <T : Any> doRequest(url: String, urlParameters: List<Pair<String, Any?>>, method: Method,
                            deserializer: ResponseDeserializable<T>): Single<T> {
        return createRequest(url = url, urlParameters = urlParameters, method = method).rx_object(
                deserializer).map {
            it.component1() ?: throw it.component2() ?: throw Exception()
        }
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
                .rx_object(deserializer).map {
                    it.component1() ?: throw it.component2() ?: throw Exception()
                }
    }

    /**
     * Do a request with a json body
     *
     * @param url the URL
     * @param method the request type (f.ex. GET)
     * @param jsonData an Object that will be serialized to json
     */
    fun doJsonRequest(url: String, method: Method,
                      jsonData: Any): Single<Pair<Response, Result<ByteArray, FuelError>>> {
        val json = Gson().toJson(jsonData)

        return createRequest(url = url, method = method).body(json).header(HEADER_CONTENT_TYPE_JSON)
                .rx_response()
    }

    companion object {
        val HEADER_CONTENT_TYPE_JSON = "Content-Type" to "application/json"
    }

}