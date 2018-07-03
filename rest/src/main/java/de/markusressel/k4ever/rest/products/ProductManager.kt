package de.markusressel.k4ever.rest.products

import com.github.kittinunf.fuel.core.Method
import de.markusressel.k4ever.rest.RequestManager
import de.markusressel.k4ever.rest.listDeserializer
import de.markusressel.k4ever.rest.products.model.ProductModel
import de.markusressel.k4ever.rest.singleDeserializer
import io.reactivex.Single

class ProductManager(val requestManager: RequestManager) : ProductApi {

    override fun getAllProducts(): Single<List<ProductModel>> {
        return requestManager
                .doRequest("/products/", Method.GET, listDeserializer())
    }

    override fun getProduct(id: Long): Single<ProductModel> {
        return requestManager
                .doRequest("/product/$id/", Method.GET, singleDeserializer())
    }

}