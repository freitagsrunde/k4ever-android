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

package de.markusressel.k4ever.view.fragment.account.purchase

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.account.PurchaseHistoryItemEntity
import de.markusressel.k4ever.data.persistence.account.PurchaseHistoryItemPersistenceManager
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.view.fragment.base.DetailContentFragmentBase
import kotlinx.android.synthetic.main.layout__item_detail__purchase.*
import javax.inject.Inject


class PurchaseDetailContentFragment : DetailContentFragmentBase<PurchaseHistoryItemEntity>() {

    override val layoutRes: Int
        get() = R.layout.layout__item_detail__purchase

    @Inject
    protected lateinit var persistenceManager: PurchaseHistoryItemPersistenceManager

    override fun getPersistenceHandler(): PersistenceManagerBase<PurchaseHistoryItemEntity> = persistenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val entity = getEntityFromPersistence()

        purchaseDate.text = entity.date.toLocaleString()

        entity.products.groupBy { it.id }.map {
            val productView = layoutInflater.inflate(R.layout.list_item__purchase_product, null)
            val productImage: ImageView = productView.findViewById(R.id.productImage)
            val productName: TextView = productView.findViewById(R.id.productName)
            val productPriceSingle: TextView = productView.findViewById(R.id.singlePrice)
            val productPriceGroup: TextView = productView.findViewById(R.id.groupPrice)

            val sampleEntity = it.value[0]

            // TODO: set product image
            productName.text = "${it.value.size}x ${sampleEntity.name}"
            productPriceSingle.text = getString(R.string.shopping_cart__item_cost, sampleEntity.price)
            productPriceGroup.text = getString(R.string.shopping_cart__item_cost, it.value.map { it.price }.sum())

            productsLayout.addView(productView)
        }

        val totalPrice = entity.products.map { it.price }.sum()
        val totalPriceText = getString(R.string.shopping_cart__item_cost, totalPrice)

        purchasePriceTotal.text = "Total: $totalPriceText"
    }

}