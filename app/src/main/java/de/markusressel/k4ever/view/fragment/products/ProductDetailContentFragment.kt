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

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import de.markusressel.k4ever.R
import de.markusressel.k4ever.business.ShoppingCart
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import de.markusressel.k4ever.data.persistence.product.ProductPersistenceManager
import de.markusressel.k4ever.view.fragment.base.DetailContentFragmentBase
import kotlinx.android.synthetic.main.layout__item_detail__product.*
import javax.inject.Inject

class ProductDetailContentFragment : DetailContentFragmentBase<ProductEntity>() {

    @Inject
    protected lateinit var persistenceManager: ProductPersistenceManager

    @Inject
    protected lateinit var shoppingCart: ShoppingCart

    override val layoutRes: Int
        get() = R.layout.layout__item_detail__product

    override fun getPersistenceHandler(): PersistenceManagerBase<ProductEntity> = persistenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val entity = getEntityFromPersistence()
        productName.text = entity.name
        productDescription.text = entity.description
        productPrice.text = getString(R.string.shopping_cart__item_cost, entity.price)
        productDeposit.text = getString(R.string.shopping_cart__item_cost, entity.deposit)
        productBarcode.text = entity.barcode

        RxView.clicks(addToCartWithout).bindToLifecycle(addToCartWithout).subscribe {
            shoppingCart.add(getEntityFromPersistence(), 1, false)
        }
        RxView.clicks(addToCartWithDeposit).bindToLifecycle(addToCartWithDeposit).subscribe {
            shoppingCart.add(getEntityFromPersistence(), 1, true)
        }
        RxView.clicks(addToCartOnlyDeposit).bindToLifecycle(addToCartOnlyDeposit).subscribe {
            Toast.makeText(context as Context, "Not (yet) supported", Toast.LENGTH_LONG).show()
            //            shoppingCart.add(getEntityFromPersistence(), 1, false)
        }
    }

}