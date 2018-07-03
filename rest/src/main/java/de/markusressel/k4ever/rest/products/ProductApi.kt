package de.markusressel.k4ever.rest.products

import de.markusressel.k4ever.rest.products.model.ProductModel
import io.reactivex.Single

interface ProductApi {

    /**
     * Get a list of all products
     */
    fun getAllProducts(): Single<List<ProductModel>>

    /**
     * Get a single product
     * @param id the id of the product
     */
    fun getProduct(id: Long): Single<ProductModel>

}