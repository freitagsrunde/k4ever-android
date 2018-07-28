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

package de.markusressel.k4ever.data.persistence.account

import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.SearchableListItem
import de.markusressel.k4ever.data.persistence.user.UserEntity
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.util.*

@Entity
data class TransferHistoryItemEntity(@Id var entityId: Long = 0, val id: Long = -1,
                                     val amount: Double = 0.0, val description: String = "",
                                     val date: Date = Date()) : IdentifiableListItem, SearchableListItem {

    lateinit var sender: ToOne<UserEntity>
    lateinit var recipient: ToOne<UserEntity>

    override fun getItemId(): Long = id

    override fun getSearchableContent(): List<Any> {
        return listOf(recipient.target.display_name, amount)
    }
}