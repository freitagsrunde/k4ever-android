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
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import de.markusressel.k4ever.data.persistence.product.ProductPersistenceManager
import de.markusressel.k4ever.view.fragment.base.DaggerSupportFragmentBase
import de.markusressel.k4ever.view.fragment.base.DetailFragmentBase
import javax.inject.Inject

/**
 * Product detail page
 *
 * Created by Markus on 07.01.2018.
 */
class ProductDetailFragment : DetailFragmentBase<ProductEntity>() {

    @Inject
    protected lateinit var persistenceManager: ProductPersistenceManager

    override val headerTextString: String
        get() = getItem().name

    override val tabItems: List<Pair<Int, () -> DaggerSupportFragmentBase>>
        get() = listOf(R.string.details to ::ProductDetailContentFragment)

    override fun getPersistenceHandler(): PersistenceManagerBase<ProductEntity> = persistenceManager

    companion object {

        private const val KEY_ID = "PRODUCT_ID"

        /**
         * Create a new instance
         */
        fun newInstance(productId: Long): ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val bundle = Bundle()
            fragment.arguments = bundle.apply {
                KEY_ID to productId
            }

            return fragment
        }
    }
}
