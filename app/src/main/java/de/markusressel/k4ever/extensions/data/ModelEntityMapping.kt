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

package de.markusressel.k4ever.extensions.data

import de.markusressel.k4ever.data.persistence.product.ProductEntity
import de.markusressel.k4ever.data.persistence.product.ProductTypeEntity
import de.markusressel.k4ever.data.persistence.user.PermissionEntity
import de.markusressel.k4ever.data.persistence.user.UserEntity
import de.markusressel.k4ever.rest.products.model.ProductModel
import de.markusressel.k4ever.rest.products.model.ProductTypeModel
import de.markusressel.k4ever.rest.users.model.PermissionModel
import de.markusressel.k4ever.rest.users.model.UserModel

fun ProductModel.toEntity(): ProductEntity {
    return ProductEntity(id = this.id,
            name = name,
            disabled = disabled,
            description = description,
            price = price,
            deposit = deposit,
            barcode = barcode,
            image = image,
            last_bought = last_bought,
            times_bought = times_bought,
            times_bought_total = times_bought_total
//            typeId = this.types.map { it.toEntity() },
//            isFavorite = this.isFavorite
    )
}

fun ProductTypeModel.toEntity(): ProductTypeEntity {
    return ProductTypeEntity(id = this.id, name = this.name, description = this.description)
}

fun UserModel.toEntity(): UserEntity {
    val permissions = this.permissions ?: emptyList()

    return UserEntity(id = this.id, display_name = this.display_name, user_name = this.name,
            balance = this.balance, permissions = permissions.map { it.toEntity() })
}

fun PermissionModel.toEntity(): PermissionEntity {
    return PermissionEntity(id = this.id, name = this.Name, description = this.Description)
}