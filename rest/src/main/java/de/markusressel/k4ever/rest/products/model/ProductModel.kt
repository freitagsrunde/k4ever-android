package de.markusressel.k4ever.rest.products.model

data class ProductModel(val id: Long, val name: String, val description: String, val price: Double, val deposit: Double, val isFavorite: Boolean)