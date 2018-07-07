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

package de.markusressel.k4ever.rest.products

import com.github.kittinunf.fuel.core.Method
import de.markusressel.k4ever.rest.RequestManager
import de.markusressel.k4ever.rest.listDeserializer
import de.markusressel.k4ever.rest.products.model.ProductModel
import de.markusressel.k4ever.rest.singleDeserializer
import io.reactivex.Single

class ProductManager(val requestManager: RequestManager) : ProductApi {

    override fun getAllProducts(): Single<List<ProductModel>> {
        return requestManager.doRequest("/products/", Method.GET, listDeserializer())
    }

    override fun getProduct(id: Long): Single<ProductModel> {
        return requestManager.doRequest("/product/$id/", Method.GET, singleDeserializer())
    }

}