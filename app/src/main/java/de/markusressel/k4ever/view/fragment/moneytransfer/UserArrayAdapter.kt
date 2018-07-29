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

package de.markusressel.k4ever.view.fragment.moneytransfer

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.user.UserEntity
import de.markusressel.k4ever.extensions.common.android.layoutInflater
import de.markusressel.k4ever.rest.K4EverRestApiClient

class UserArrayAdapter(context: Context, private val restClient: K4EverRestApiClient,
                       private val currentItems: MutableList<UserEntity> = mutableListOf()) : ArrayAdapter<UserEntity>(
        context, LAYOUT_RES, currentItems) {

    private var originalItems = emptyList<UserEntity>()

    private val filter = UserArrayFilter()

    /**
     * Set the list of items to autocomplete
     */
    fun setItems(items: List<UserEntity>) {
        val comparator = compareBy<UserEntity> {
            it.display_name
        }.thenBy { it.user_name }

        val sortedUsers = items.sortedWith(comparator)

        originalItems = ArrayList(sortedUsers)

        currentItems.clear()
        currentItems.addAll(originalItems)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter = filter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val itemView = convertView ?: context.layoutInflater().inflate(LAYOUT_RES, parent, false)

        val user = getItem(position)

        val userAvatar: SimpleDraweeView = itemView.findViewById(R.id.userAvatar)
        userAvatar.setImageURI(restClient.getUserAvatarURL(user.id))

        val userName: TextView = itemView.findViewById(R.id.userName)
        userName.text = "${user.display_name} (${user.user_name})"

        return itemView
    }

    /**
     *
     * An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.
     */
    private inner class UserArrayFilter : Filter() {

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return if (resultValue is UserEntity) {
                "${resultValue.display_name} (${resultValue.user_name})"
            } else {
                return super.convertResultToString(resultValue)
            }
        }

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()

            if (constraint.isNullOrEmpty()) {
                results.values = originalItems
                results.count = originalItems.count()
                return results
            }

            val filteredUsers = originalItems.filter {
                it.getSearchableContent().any {
                    it.toString().contains(constraint!!, ignoreCase = true)
                }
            }

            results.values = filteredUsers
            results.count = filteredUsers.count()

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
            currentItems.clear()
            currentItems.addAll(results.values as List<UserEntity>)

            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

    companion object {
        val LAYOUT_RES = R.layout.layout__autocomplete_user
    }

}