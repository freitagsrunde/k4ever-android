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
import com.github.kittinunf.fuel.core.Method
import de.markusressel.k4ever.rest.RequestManager
import de.markusressel.k4ever.rest.listDeserializer
import de.markusressel.k4ever.rest.singleDeserializer
import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.rest.users.model.PurchaseHistoryItemModel
import de.markusressel.k4ever.rest.users.model.TransferHistoryItemModel
import de.markusressel.k4ever.rest.users.model.UserModel

class UserManager(val requestManager: RequestManager) : UserApi {

    override suspend fun getAllUsers(): List<UserModel> {
        return requestManager.awaitRequest("/users/", Method.GET, listDeserializer())
    }

    override suspend fun getUser(id: Long): UserModel {
        return requestManager.awaitRequest("/user/$id/", Method.GET, singleDeserializer())
    }

    override suspend fun getBalanceHistory(id: Long): List<BalanceHistoryItemModel> {
        return requestManager.awaitRequest("/user/$id/balance_history/", Method.GET,
                singleDeserializer())
    }

    override suspend fun getPurchaseHistory(id: Long): List<PurchaseHistoryItemModel> {
        return requestManager.awaitRequest("/user/$id/purchase_history/", Method.GET,
                singleDeserializer())
    }

    override suspend fun getTransferHistory(id: Long): List<TransferHistoryItemModel> {
        return requestManager.awaitRequest("/user/$id/transfer_history/", Method.GET,
                singleDeserializer())
    }

    override suspend fun getUserAvatar(id: Long): Drawable {
        throw NotImplementedError()
    }

    override suspend fun getUserAvatarURL(id: Long): String {
        return "${requestManager.hostname}/user/$id/image/"
    }

}