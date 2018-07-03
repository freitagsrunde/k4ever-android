package de.markusressel.k4ever.business

import de.markusressel.k4ever.rest.products.model.ProductModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingBag @Inject constructor() {

    /**
     * Map of shoppingBag and their amount
     */
    val shoppingBag: MutableMap<ProductModel, Int> = mutableMapOf()

    /**
     * Add a product to the shopping bag
     *
     * @param product the product to add
     * @param amount the amount of items to add
     */
    fun add(product: ProductModel, amount: Int) {
        val currentAmount = shoppingBag.getOrPut(product) { 0 }
        val newAmount = currentAmount + amount
        shoppingBag[product] = newAmount
    }

    /**
     * Remove a product from the shopping bag
     *
     * @param product the product to remove
     * @param amount the amount to remove
     */
    fun remove(product: ProductModel, amount: Int) {
        val currentAmount = shoppingBag.getOrPut(product) { amount }
        val newAmount = currentAmount - amount

        if (newAmount <= 0) {
            shoppingBag.remove(product)
        } else {
            shoppingBag[product] = newAmount
        }
    }

}