package de.markusressel.k4ever.rest.users.model

import de.markusressel.k4ever.rest.products.model.ProductModel
import java.util.*

data class PurchaseHistoryItemModel(val id: Long, val products: List<ProductModel>, val date: Date)