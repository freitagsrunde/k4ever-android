package de.markusressel.k4ever.rest.users

import com.github.kittinunf.fuel.core.Method
import de.markusressel.k4ever.rest.RequestManager
import de.markusressel.k4ever.rest.listDeserializer
import de.markusressel.k4ever.rest.singleDeserializer
import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.rest.users.model.PurchaseHistoryItemModel
import de.markusressel.k4ever.rest.users.model.UserModel
import io.reactivex.Single

class UserManager(val requestManager: RequestManager) : UserApi {

    override fun getAllUsers(): Single<List<UserModel>> {
        return requestManager
                .doRequest("/users/", Method.GET, listDeserializer())
    }

    override fun getUser(id: Long): Single<UserModel> {
        return requestManager
                .doRequest("/user/$id/", Method.GET, singleDeserializer())
    }

    override fun getBalanceHistory(id: Long): Single<List<BalanceHistoryItemModel>> {
        return requestManager
                .doRequest("/user/$id/balance_history/", Method.GET, singleDeserializer())
    }

    override fun getPurchaseHistory(id: Long): Single<List<PurchaseHistoryItemModel>> {
        return requestManager
                .doRequest("/user/$id/purchase_history/", Method.GET, singleDeserializer())
    }

}