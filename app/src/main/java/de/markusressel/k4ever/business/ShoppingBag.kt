package de.markusressel.k4ever.business

import com.eightbitlab.rxbus.Bus
import de.markusressel.k4ever.ProductEntity
import de.markusressel.k4ever.event.ProductAddedToShoppingBagEvent
import de.markusressel.k4ever.event.ProductRemovedFromShoppingBagEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingBag @Inject constructor() {

    /**
     * Map of shoppingBag and their amount
     */
    val shoppingBag: MutableMap<ProductEntity, Int> = mutableMapOf()

    /**
     * Add a product to the shopping bag
     *
     * @param product the product to add
     * @param amount the amount of items to add
     */
    fun add(product: ProductEntity, amount: Int) {
        val currentAmount = shoppingBag.getOrPut(product) { 0 }
        val newAmount = currentAmount + amount
        shoppingBag[product] = newAmount

        Bus.send(ProductAddedToShoppingBagEvent(product, amount))
    }

    /**
     * Remove a product from the shopping bag
     *
     * @param product the product to remove
     * @param amount the amount to remove
     */
    fun remove(product: ProductEntity, amount: Int) {
        val currentAmount = shoppingBag.getOrPut(product) { amount }
        val newAmount = currentAmount - amount

        if (newAmount <= 0) {
            shoppingBag.remove(product)
        } else {
            shoppingBag[product] = newAmount
        }

        Bus.send(ProductRemovedFromShoppingBagEvent(product, amount))
    }

}