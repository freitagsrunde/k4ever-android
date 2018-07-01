package de.markusressel.k4ever.rest

/**
 * Convenience delegation class for easy access to all api methods
 *
 * Created by Markus on 03.06.2018.
 */
class K4EverRestClient(private val requestManager: RequestManager = RequestManager()) {

    /**
     * Set the hostname for this client
     */
    fun setHostname(hostname: String) {
        requestManager
                .hostname = hostname
    }

    /**
     * Set the api resource for this client (in case it is not the default "/")
     */
    fun setApiResource(apiResource: String) {
        requestManager
                .apiResource = apiResource
    }

    /**
     * Set the BasicAuthConfig for this client
     */
    fun getBasicAuthConfig(): BasicAuthConfig? {
        return requestManager
                .basicAuthConfig
    }

    /**
     * Set the BasicAuthConfig for this client
     */
    fun setBasicAuthConfig(basicAuthConfig: BasicAuthConfig) {
        requestManager
                .basicAuthConfig = basicAuthConfig
    }

}