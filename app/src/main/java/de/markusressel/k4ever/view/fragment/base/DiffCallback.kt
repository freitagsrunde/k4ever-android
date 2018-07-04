package de.markusressel.k4ever.view.fragment.base

import android.support.v7.util.DiffUtil
import de.markusressel.k4ever.data.persistence.PersistenceEntity

/**
 * Generic callback used to compare list items
 */
class DiffCallback<T : PersistenceEntity>(private val oldListItems: List<T>, private val newListItems: List<T>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldListItems.size
    }

    override fun getNewListSize(): Int {
        return newListItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldListItems[oldItemPosition]
        val newItem = newListItems[newItemPosition]

        return oldItem.javaClass == newItem.javaClass && oldItem.getItemId() == newItem.getItemId()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldListItems[oldItemPosition]
        val newItem = newListItems[newItemPosition]

        return oldItem == newItem
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}
