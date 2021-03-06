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

package de.markusressel.k4ever.business

import com.github.ajalt.timberkt.Timber
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingCart @Inject constructor() {

    /**
     * List of items currently in the shopping cart
     */
    val items: MutableList<ShoppingCartItem> = mutableListOf()

    /**
     * Add a product to the shopping cart
     *
     * @param product the product to add
     * @param amount the amount of items to add
     * @param withDeposit true, if the item should be added with deposit, false otherwise
     */
    @Synchronized
    fun add(product: ProductEntity, amount: Int, withDeposit: Boolean) {
        val matchingItem = items.firstOrNull {
            it.product.id == product.id && it.withDeposit == withDeposit
        }

        if (matchingItem != null) {
            matchingItem.amount += amount
        } else {
            val item = ShoppingCartItem(idCounter.getAndIncrement(), product, amount, withDeposit)
            items.add(item)
        }
    }

    /**
     * Set the amount of products to the exact value
     *
     * @param product the product to set a value for
     * @param amount the amount to set
     * @param withDeposit if the item was/will be added with deposit
     * @return true, if the the shopping cart changed, false otherwise
     */
    @Synchronized
    fun set(product: ProductEntity, amount: Int, withDeposit: Boolean): Boolean {
        val matchingItem = items.firstOrNull {
            it.product.id == product.id && it.withDeposit == withDeposit
        }

        if (matchingItem != null) {
            return when {
                matchingItem.amount == amount -> false
                amount <= 0 -> items.remove(matchingItem)
                else -> {
                    matchingItem.amount = amount
                    true
                }
            }
        } else {
            return if (amount <= 0) {
                false
            } else {
                add(product, amount, withDeposit)
                true
            }
        }
    }

    /**
     * Remove a product from the shopping cart
     *
     * @param product the product to remove
     * @param amount the amount to remove
     * @param withDeposit true, if the item was added with deposit, false otherwise
     * @return true, if the the shopping cart changed, false otherwise
     */
    @Synchronized
    fun remove(product: ProductEntity, amount: Int, withDeposit: Boolean): Boolean {
        val matchingItem = items.firstOrNull {
            it.product.id == product.id && it.withDeposit == withDeposit
        }

        return if (matchingItem != null) {
            if (amount >= matchingItem.amount) {
                items.remove(matchingItem)
            } else {
                matchingItem.amount -= amount
            }
            true
        } else {
            Timber.e { "Unable to find matching item in shopping cart!" }
            false
        }
    }

    /**
     * @return the total amount of individual items currently in shopping cart
     */
    fun getTotalItemCount(): Int {
        return items.map {
            it.amount
        }.sum()
    }

    /**
     * @return the total cost of all items currently in shopping cart
     */
    fun getTotalPrice(): Double {
        return items.map {
            it.getPrice()
        }.sum()
    }

    /**
     * @return true if no items are currently in the shopping cart
     */
    fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    companion object {
        private val idCounter = AtomicLong(1)
    }

}