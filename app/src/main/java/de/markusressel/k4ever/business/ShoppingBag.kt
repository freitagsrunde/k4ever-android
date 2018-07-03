package de.markusressel.k4ever.business

import com.github.ajalt.timberkt.Timber
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingBag @Inject constructor() {

    /**
     * Map of items and their amount
     */
    val items: MutableList<ShoppingBagItem> = mutableListOf()

    /**
     * Add a product to the shopping bag
     *
     * @param product the product to add
     * @param amount the amount of items to add
     * @param withDeposit true, if the item should be added with deposit, false otherwise
     */
    fun add(product: ProductEntity, amount: Int, withDeposit: Boolean) {
        val matchingItem = items.firstOrNull {
            it.product == product && it.withDeposit == withDeposit
        }

        if (matchingItem != null) {
            matchingItem.amount += amount
        } else {
            val item = ShoppingBagItem(product, amount, withDeposit)
            items.add(item)
        }
    }

    /**
     * Remove a product from the shopping bag
     *
     * @param product the product to remove
     * @param amount the amount to remove
     * @param withDeposit true, if the item was added with deposit, false otherwise
     */
    fun remove(product: ProductEntity, amount: Int, withDeposit: Boolean) {
        var amountToRemove = amount

        val matchingItem = items.firstOrNull {
            it.product == product && it.withDeposit == withDeposit
        }

        if (matchingItem != null) {
            if (amountToRemove >= matchingItem.amount) {
                items.remove(matchingItem)
            } else {
                matchingItem.amount -= amountToRemove
            }
            matchingItem.amount -= amount
        } else {
            Timber.e { "Unable to find matching item in shopping bag!" }
        }
    }

    /**
     * @return the total amount of individual items currently in shopping bag
     */
    fun getTotalItemCount(): Int {
        return items.map {
            it.amount
        }.sum()
    }

    /**
     * @return the total cost of all items currently in shopping bag
     */
    fun getTotalPrice(): Double {
        return items.map {
            var itemPrice = it.product.price
            if (it.withDeposit) {
                itemPrice += it.product.deposit
            }

            itemPrice * it.amount
        }.sum()
    }

}