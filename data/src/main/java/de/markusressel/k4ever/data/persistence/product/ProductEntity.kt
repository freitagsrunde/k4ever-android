/*
 * Copyright (C) 2018 Markus Ressel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.markusressel.k4ever.data.persistence.product

import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.SearchableListItem
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class ProductEntity(@Id(assignable = true) var id: Long = -1L,
                         val name: String = "",
                         val disabled: Boolean = false,
                         val description: String = "",
                         val price: Double = 0.0,
                         val deposit: Double = 0.0,
                         val barcode: String? = null,
                         val image: String? = null,
                         val last_bought: Date? = null,
                         val times_bought: Int = 0,
                         val times_bought_total: Int = 0,
                         var isFavorite: Boolean = false
) : IdentifiableListItem, SearchableListItem {

    override fun getItemId(): Long = id

    override fun getSearchableContent(): List<Any> {
        return listOf(name, description)
    }

}