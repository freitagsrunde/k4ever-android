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

import com.github.kittinunf.fuel.core.Method
import de.markusressel.k4ever.rest.products.ProductApi
import de.markusressel.k4ever.rest.products.ProductManager
import de.markusressel.k4ever.rest.users.UserApi
import de.markusressel.k4ever.rest.users.UserManager

/**
 * Convenience delegation class for easy access to all api methods
 *
 * Created by Markus on 03.06.2018.
 */
class K4EverRestClient(private val requestManager: RequestManager = RequestManager(),
                       productManager: ProductApi = ProductManager(requestManager),
                       userManager: UserApi = UserManager(requestManager))
    : K4EverRestApiClient, ProductApi by productManager, UserApi by userManager {

    /**
     * Set the hostname for this client
     */
    override fun setHostname(hostname: String) {
        requestManager.hostname = hostname
    }

    /**
     * Set the BasicAuthConfig for this client
     */
    override fun getBasicAuthConfig(): BasicAuthConfig? {
        return requestManager.basicAuthConfig
    }

    /**
     * Set the BasicAuthConfig for this client
     */
    override fun setBasicAuthConfig(basicAuthConfig: BasicAuthConfig) {
        requestManager.basicAuthConfig = basicAuthConfig
    }

    override suspend fun checkLogin(username: String, password: String): Boolean {
        return kotlin.runCatching {
            requestManager.login(username, password)
        }.isSuccess
    }

    override suspend fun getVersion(): VersionModel {
        return requestManager.awaitRequest("/version/", Method.GET, singleDeserializer())
    }

}