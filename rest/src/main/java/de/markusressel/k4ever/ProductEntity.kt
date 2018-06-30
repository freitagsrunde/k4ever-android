package de.markusressel.k4ever

import android.graphics.drawable.Drawable

data class ProductEntity(val id: Long, val name: String, val description: String, val price: Double, val deposit: Double?, val previewImage: Drawable)