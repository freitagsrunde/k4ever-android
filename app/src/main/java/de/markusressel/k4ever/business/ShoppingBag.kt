package de.markusressel.k4ever.business

import de.markusressel.k4ever.data.persistence.product.ProductEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingBag @Inject constructor() {

    /**
     * Map of items and their amount
     */
    val items: MutableMap<ProductEntity, Int> = mutableMapOf()

    /**
     * Add a product to the shopping bag
     *
     * @param product the product to add
     * @param amount the amount of items to add
     */
    fun add(product: ProductEntity, amount: Int) {
        val currentAmount = items.getOrPut(product) { 0 }
        val newAmount = currentAmount + amount
        items[product] = newAmount
    }

    /**
     * Remove a product from the shopping bag
     *
     * @param product the product to remove
     * @param amount the amount to remove
     */
    fun remove(product: ProductEntity, amount: Int) {
        val currentAmount = items.getOrPut(product) { amount }
        val newAmount = currentAmount - amount

        if (newAmount <= 0) {
            items.remove(product)
        } else {
            items[product] = newAmount
        }
    }

}