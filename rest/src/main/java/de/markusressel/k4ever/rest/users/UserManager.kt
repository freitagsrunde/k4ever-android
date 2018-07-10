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

import com.github.kittinunf.fuel.core.Method
import de.markusressel.k4ever.rest.RequestManager
import de.markusressel.k4ever.rest.listDeserializer
import de.markusressel.k4ever.rest.singleDeserializer
import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.rest.users.model.PurchaseHistoryItemModel
import de.markusressel.k4ever.rest.users.model.TransferHistoryItemModel
import de.markusressel.k4ever.rest.users.model.UserModel
import io.reactivex.Single

class UserManager(val requestManager: RequestManager) : UserApi {

    override fun getAllUsers(): Single<List<UserModel>> {
        return requestManager.doRequest("/users/", Method.GET, listDeserializer())
    }

    override fun getUser(id: Long): Single<UserModel> {
        return requestManager.doRequest("/user/$id/", Method.GET, singleDeserializer())
    }

    override fun getBalanceHistory(id: Long): Single<List<BalanceHistoryItemModel>> {
        return requestManager.doRequest("/user/$id/balance_history/", Method.GET, singleDeserializer())
    }

    override fun getPurchaseHistory(id: Long): Single<List<PurchaseHistoryItemModel>> {
        return requestManager.doRequest("/user/$id/purchase_history/", Method.GET, singleDeserializer())
    }

    override fun getTransferHistory(id: Long): Single<List<TransferHistoryItemModel>> {
        return requestManager.doRequest("/user/$id/transfer_history/", Method.GET,
                singleDeserializer())
    }

}