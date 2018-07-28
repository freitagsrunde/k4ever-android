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

package de.markusressel.k4ever.rest.users

import android.graphics.drawable.Drawable
import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.rest.users.model.PurchaseHistoryItemModel
import de.markusressel.k4ever.rest.users.model.TransferHistoryItemModel
import de.markusressel.k4ever.rest.users.model.UserModel
import io.reactivex.Single

interface UserApi {

    /**
     * Get a list of all users
     */
    fun getAllUsers(): Single<List<UserModel>>

    /**
     * Get a single user
     * @param id the id of the product
     */
    fun getUser(id: Long): Single<UserModel>

    /**
     * Get a list of all balance history items of a specific user
     * @param id the id of the user
     */
    fun getBalanceHistory(id: Long): Single<List<BalanceHistoryItemModel>>

    /**
     * Get a list of all purchase history items of a specific user
     * @param id the id of the user
     */
    fun getPurchaseHistory(id: Long): Single<List<PurchaseHistoryItemModel>>

    /**
     * Get a list of all transfer history items of a specific user
     * @param id the id of the user
     */
    fun getTransferHistory(id: Long): Single<List<TransferHistoryItemModel>>

    /**
     * Download the avatar of a user
     */
    fun getUserAvatar(id: Long): Single<Drawable>

    /**
     * Get the URL to download the avatar of a user
     */
    fun getUserAvatarURL(id: Long): String

}