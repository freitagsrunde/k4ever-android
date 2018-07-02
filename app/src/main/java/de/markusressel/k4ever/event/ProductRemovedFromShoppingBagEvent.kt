package de.markusressel.k4ever.event

import de.markusressel.k4ever.ProductEntity

data class ProductRemovedFromShoppingBagEvent(val product: ProductEntity, val amount: Int)