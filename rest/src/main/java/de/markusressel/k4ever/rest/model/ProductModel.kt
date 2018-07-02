package de.markusressel.k4ever.rest.model

import android.graphics.drawable.Drawable

data class ProductModel(val id: Long, val name: String, val description: String, val price: Double, val deposit: Double, val previewImage: Drawable)