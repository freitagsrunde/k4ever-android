package de.markusressel.k4ever.business

import de.markusressel.k4ever.data.persistence.product.ProductEntity

data class ShoppingBagItem(val product: ProductEntity, var amount: Int, val withDeposit: Boolean)

/**
 * Get the price for a shopping bag item, taking amount and deposit into account
 */
fun ShoppingBagItem.getPrice(): Double {
    var price = product.price
    if (this.withDeposit) {
        price += product.deposit
    }

    return price * amount
}