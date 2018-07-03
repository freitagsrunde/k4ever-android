package de.markusressel.k4ever.business

import de.markusressel.k4ever.data.persistence.product.ProductEntity

data class ShoppingBagItem(val product: ProductEntity, var amount: Int, val withDeposit: Boolean)