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

package de.markusressel.k4ever.view.fragment.products

import android.os.Bundle
import android.view.View
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import de.markusressel.k4ever.data.persistence.product.ProductPersistenceManager
import de.markusressel.k4ever.view.fragment.base.DetailContentFragmentBase
import kotlinx.android.synthetic.main.layout__item_detail__product.*
import javax.inject.Inject

class ProductDetailContentFragment : DetailContentFragmentBase<ProductEntity>() {

    @Inject
    protected lateinit var persistenceManager: ProductPersistenceManager

    override val layoutRes: Int
        get() = R.layout.layout__item_detail__product

    override fun getPersistenceHandler(): PersistenceManagerBase<ProductEntity> = persistenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val entity = getEntityFromPersistence()
        productName.text = entity.name

        productDescription.text = entity.description
    }

}