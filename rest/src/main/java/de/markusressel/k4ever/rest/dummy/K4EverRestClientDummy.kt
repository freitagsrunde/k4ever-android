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

import de.markusressel.k4ever.rest.BasicAuthConfig
import de.markusressel.k4ever.rest.K4EverRestApiClient
import de.markusressel.k4ever.rest.products.model.ProductModel
import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.rest.users.model.PurchaseHistoryItemModel
import de.markusressel.k4ever.rest.users.model.TransferHistoryItemModel
import de.markusressel.k4ever.rest.users.model.UserModel
import io.reactivex.Single
import java.util.*

class K4EverRestClientDummy : K4EverRestApiClient {
    override fun getAllProducts(): Single<List<ProductModel>> {
        val p1 = ProductModel(0, "Mio Mate", "Getränk der Studenten", 1.0, 0.2, true)
        val p2 = ProductModel(1, "Club Mate", "Getränk der Studenten", 0.8, 0.2, true)
        val p3 = ProductModel(2, "Cola", "Zucker", 1.0, 0.2, false)
        val p4 = ProductModel(3, "Spezi", "Zucker ^2", 1.0, 0.2, false)
        val p5 = ProductModel(4, "Snickers", "Zucker ^5", 1.0, 0.0, false)
        val p6 = ProductModel(5, "Coca Cola Zero ®",
                "Spritziges Erfrischungsgetränk, so schwarz wie deine Seele \uD83D\uDE08", 1.1,
                0.25, true)

        val fakeRealItems = listOf(p1, p2, p3, p4, p5, p6)
        val randomItems = 10.rangeTo(1000).map { it.toLong() }.map {
            ProductModel(it, "Product $it", "Product description ($it)", 1.0 + it.toDouble() / 100,
                    0.2 + it.toDouble() / 100, false)
        }

        return Single.just(listOf(fakeRealItems, randomItems).flatten().shuffled())
    }

    override fun getProduct(id: Long): Single<ProductModel> {
        val productModel = getAllProducts().blockingGet().find { it.id == id }
        return Single.just(productModel)
    }

    override fun getAllUsers(): Single<List<UserModel>> {
        val p1 = UserModel(0, "g_markus", "Markus Ressel")
        val p2 = UserModel(1, "max", "Max Rosin")
        val p3 = UserModel(2, "phillip", "Zucker")

        val fakeRealItems = listOf(p1, p2, p3)

        val randomItems = 10.rangeTo(1000).map { it.toLong() }.map {
            UserModel(it, "User_$it", "User $it")
        }

        return Single.just(listOf(fakeRealItems, randomItems).flatten().shuffled())
    }

    override fun getUser(id: Long): Single<UserModel> {
        val productModel = getAllUsers().blockingGet().find { it.id == id }
        return Single.just(productModel)
    }

    override fun getBalanceHistory(id: Long): Single<List<BalanceHistoryItemModel>> {
        val randomItems = 10.rangeTo(1000).map { it.toLong() }.map {
            BalanceHistoryItemModel(it, it.toDouble(), Date(Date().time + it))
        }

        return Single.just(randomItems.shuffled())
    }

    override fun getPurchaseHistory(id: Long): Single<List<PurchaseHistoryItemModel>> {
        val randomItems = 10.rangeTo(1000).map { it.toLong() }.map {
            PurchaseHistoryItemModel(it, listOf(getProduct(it).blockingGet()),
                    Date(Date().time + it))
        }

        return Single.just(randomItems.shuffled())
    }

    override fun getTransferHistory(id: Long): Single<List<TransferHistoryItemModel>> {
        val randomItems = 10.rangeTo(1000).map { it.toLong() }.map {
            TransferHistoryItemModel(it, 5.0, getUser(1).blockingGet(), Date(Date().time + it))
        }

        return Single.just(randomItems.shuffled())
    }

    override fun setHostname(hostname: String) {
    }

    override fun getBasicAuthConfig(): BasicAuthConfig? {
        return null
    }

    override fun setBasicAuthConfig(basicAuthConfig: BasicAuthConfig) {
    }
}