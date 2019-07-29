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

package de.markusressel.k4ever.rest.dummy

import android.graphics.drawable.Drawable
import de.markusressel.k4ever.rest.BasicAuthConfig
import de.markusressel.k4ever.rest.K4EverRestApiClient
import de.markusressel.k4ever.rest.R
import de.markusressel.k4ever.rest.VersionModel
import de.markusressel.k4ever.rest.products.model.ProductModel
import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.rest.users.model.PurchaseHistoryItemModel
import de.markusressel.k4ever.rest.users.model.TransferHistoryItemModel
import de.markusressel.k4ever.rest.users.model.UserModel
import kotlinx.coroutines.runBlocking
import java.util.*

class K4EverRestClientDummy : K4EverRestApiClient {

    override suspend fun getVersion(): VersionModel {
        return VersionModel(
                "0.0.1",
                "HEAD",
                "6b850d0aefe23a11cd5d6622bb2e07d9b428544c",
                Calendar.getInstance().time
        )
    }

    override suspend fun getUserAvatar(id: Long): Drawable {
        throw NotImplementedError()
    }

    override suspend fun getUserAvatarURL(id: Long): String {
        val userName = getUser(id)?.user_name

        return when (userName) {
            "g_markus" -> "android.resource://de.markusressel.k4ever/${R.drawable.demo_avatar__markus}"
            else -> "https://api.adorable.io/avatars/285/$userName.png"
            //            else -> "https://ui-avatars.com/api/?name=$userName"
        }
    }

    companion object {
        private val p1 = ProductModel(1, "Mio Mate", "Getränk der Studenten", 1.0, 0.2, "000000000",
                listOf(), true)
        private val p2 = ProductModel(2, "Club Mate", "Getränk der Studenten", 0.8, 0.2,
                "000000001", listOf(), true)
        private val p3 = ProductModel(3, "Cola", "Zucker", 1.0, 0.2, "000000002", listOf(), false)
        private val p4 = ProductModel(4, "Spezi", "Zucker ^2", 1.0, 0.2, "000000003", listOf(),
                false)
        private val p5 = ProductModel(5, "Snickers", "Zucker ^5", 1.0, 0.0, "000000004", listOf(),
                false)
        private val p6 = ProductModel(6, "Coca Cola Zero ®",
                "Spritziges Erfrischungsgetränk, so schwarz wie deine Seele \uD83D\uDE08", 1.1,
                0.25, "000000005", listOf(), true)

        private val fakeRealProducts = listOf(p1, p2, p3, p4, p5, p6)
        private val randomProducts = ((fakeRealProducts.size + 1)..100L).map {
            ProductModel(it, "Product $it", "Product description ($it)", 1.0 + it.toDouble() / 100,
                    0.2 + it.toDouble() / 100, "$it", listOf(), false)
        }

        private val products = listOf(fakeRealProducts, randomProducts).flatten()


        private val u1 = UserModel(1, "g_markus", "Markus Ressel", 20.0, listOf())
        private val u2 = UserModel(2, "max", "Max Rosin", -50.0, listOf())
        private val u3 = UserModel(3, "phillip", "Phillip", 0.0, listOf())

        private val fakeRealUsers = listOf(u1, u2, u3)

        private val randomUsers = (10L..100).map {
            UserModel(it, "User_$it", "User $it", it.toDouble(), listOf())
        }

        private val users = listOf(fakeRealUsers, randomUsers).flatten()


        private val balanceItems = (10L..100).map {
            BalanceHistoryItemModel(it,
                    (1 - (it % 2) * 2) * (it % 24 + 1) / 10.toDouble(),
                    Date(Date().time + it))
        }

    }

    private val purchaseItems = (10L..100).map {
        runBlocking {
            val products = (1L..100).shuffled().take((1..10).random()).map {
                val product = getProduct(it)!!
                val list = mutableListOf(product)
                repeat((1..3).random()) {
                    list.add(product)
                }
                list
            }.flatten()

            PurchaseHistoryItemModel(it,
                    products,
                    Date(Date().time + it))
        }
    }

    private val transferItems = (10L..100).map {
        val userIdFrom = (1L..3).random()
        var userIdTo = userIdFrom
        while (userIdTo == userIdFrom) {
            userIdTo = (1L..3).random()
        }

        runBlocking {
            TransferHistoryItemModel(it, 5.0,
                    "Money transfer description #$it",
                    getUser(userIdFrom)!!,
                    getUser(userIdTo)!!,
                    Date(Date().time + it))
        }
    }

    override suspend fun getAllProducts(): List<ProductModel> {
        return products.shuffled()
    }

    override suspend fun getProduct(id: Long): ProductModel? {
        return getAllProducts().find { it.id == id }
    }

    override suspend fun getAllUsers(): List<UserModel> {
        return users.shuffled()
    }

    override suspend fun getUser(id: Long): UserModel? {
        return getAllUsers().find { it.id == id }

    }

    override suspend fun getBalanceHistory(id: Long): List<BalanceHistoryItemModel> {
        return balanceItems.shuffled()
    }

    override suspend fun getPurchaseHistory(id: Long): List<PurchaseHistoryItemModel> {
        return purchaseItems.shuffled()
    }

    override suspend fun getTransferHistory(id: Long): List<TransferHistoryItemModel> {
        return transferItems.shuffled()
    }

    override fun setHostname(hostname: String) {
    }

    override fun getBasicAuthConfig(): BasicAuthConfig? {
        return null
    }

    override fun setBasicAuthConfig(basicAuthConfig: BasicAuthConfig) {
    }
}