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

package de.markusressel.k4ever.rest.products.model

import java.util.*

data class ProductModel(val id: Long,
                        val name: String,
                        val disabled: Boolean,
                        val description: String,
                        val price: Double,
                        val deposit: Double,
                        val barcode: String?,
                        val image: String?,
                        val last_bought: Date?,
                        val times_bought: Int,
                        val times_bought_total: Int
//                        val types: List<ProductTypeModel>,
//                        val isFavorite: Boolean
)