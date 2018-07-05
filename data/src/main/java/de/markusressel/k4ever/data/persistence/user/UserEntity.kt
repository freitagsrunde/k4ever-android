package de.markusressel.k4ever.data.persistence.user

import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.SearchableListItem
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class UserEntity(@Id var entityId: Long = 0, val id: Long, val user_name: String, val display_name: String) : IdentifiableListItem, SearchableListItem {
    override fun getItemId(): Long = id
    override fun getSearchableContent(): List<Any> = listOf(user_name, display_name)
}