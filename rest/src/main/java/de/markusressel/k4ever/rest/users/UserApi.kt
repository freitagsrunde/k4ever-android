package de.markusressel.k4ever.rest.users

import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.rest.users.model.PurchaseHistoryItemModel
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

}