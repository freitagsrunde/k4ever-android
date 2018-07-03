package de.markusressel.k4ever.data.persistence.product

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ProductEntity(@Id var entityId: Long = 0, val id: Long, val name: String, val description: String, val price: Double, val deposit: Double)