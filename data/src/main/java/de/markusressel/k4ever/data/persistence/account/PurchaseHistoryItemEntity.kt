package de.markusressel.k4ever.data.persistence.account

import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.SearchableListItem
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class PurchaseHistoryItemEntity(@Id var entityId: Long = 0, val id: Long, val products: List<ProductEntity>, val date: Date) : IdentifiableListItem, SearchableListItem {
    override fun getItemId(): Long = id
}