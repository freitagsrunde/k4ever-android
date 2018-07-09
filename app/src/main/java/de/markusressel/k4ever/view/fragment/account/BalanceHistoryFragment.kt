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

package de.markusressel.k4ever.view.fragment.account

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic
import de.markusressel.k4ever.BR
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.account.BalanceHistoryItemEntity
import de.markusressel.k4ever.data.persistence.account.BalanceHistoryItemPersistenceManager
import de.markusressel.k4ever.data.persistence.account.PurchaseHistoryItemEntity
import de.markusressel.k4ever.data.persistence.account.PurchaseHistoryItemPersistenceManager
import de.markusressel.k4ever.databinding.ListItemBalanceHistoryItemBinding
import de.markusressel.k4ever.databinding.ListItemPurchaseHistoryItemBinding
import de.markusressel.k4ever.extensions.filterByExpectedType
import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.rest.users.model.PurchaseHistoryItemModel
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import de.markusressel.k4ever.view.fragment.base.FabConfig
import de.markusressel.k4ever.view.fragment.base.MultiPersistableListFragmentBase
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment__account__balance_history.*
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import javax.inject.Inject


/**
 * Server Status fragment
 *
 * Created by Markus on 07.01.2018.
 */
class BalanceHistoryFragment : MultiPersistableListFragmentBase() {

    override val layoutRes: Int
        get() = R.layout.fragment__account__balance_history

    override fun getLeftFabs(): List<FabConfig.Fab> {
        return listOf(FabConfig.Fab(description = R.string.withdraw_money,
                icon = MaterialDesignIconic.Icon.gmi_minus, onClick = {
            // TODO: open "withdraw money" dialog
        }), FabConfig.Fab(description = R.string.deposit_money,
                icon = MaterialDesignIconic.Icon.gmi_plus, onClick = {
            // TODO: open "deposit money" dialog
        }))
    }

    override fun getRightFabs(): List<FabConfig.Fab> {
        return listOf()
    }

    @Inject
    lateinit var balancePersistenceManager: BalanceHistoryItemPersistenceManager

    @Inject
    lateinit var purchasePersistenceManager: PurchaseHistoryItemPersistenceManager

    override fun createAdapter(): LastAdapter {
        return LastAdapter(listValues,
                BR.item).map<BalanceHistoryItemEntity, ListItemBalanceHistoryItemBinding>(
                R.layout.list_item__balance_history_item) {
            onCreate {
                it.binding.presenter = this@BalanceHistoryFragment
            }
            onClick {
                // TODO: should there be any detail view of balance history items?
                //                        openDetailView(listValues[it.adapterPosition])
            }
        }
                .map<PurchaseHistoryItemEntity, ListItemPurchaseHistoryItemBinding>(
                        R.layout.list_item__purchase_history_item) {
                    onCreate {
                        it.binding.presenter = this@BalanceHistoryFragment
                    }
                    onClick {
                        // TODO: should there be any detail view of purchase history items?
                        //                        openDetailView(listValues[it.adapterPosition])
                    }
                }.into(recyclerView)
    }

    override fun getLoadDataFromSourceFunction(): Single<List<IdentifiableListItem>> {
        val currentUserId = 1L

        // downloading all items even though they have different types would be possible
        return Single.zip(restClient.getBalanceHistory(currentUserId),
                restClient.getPurchaseHistory(currentUserId),
                BiFunction<List<BalanceHistoryItemModel>, List<PurchaseHistoryItemModel>, List<IdentifiableListItem>> { t1, t2 ->
                    val bhiEntities = t1.map {
                        BalanceHistoryItemEntity(id = it.id, amount = it.amount, date = it.date)
                    }
                    val phiEntities = t1.map {
                        PurchaseHistoryItemEntity(id = it.id, products = emptyList(),
                                date = it.date)
                    }

                    listOf(bhiEntities, phiEntities).flatten()
                })
    }

    override fun mapToEntity(it: Any): IdentifiableListItem {
        return when (it) {
            is BalanceHistoryItemModel -> BalanceHistoryItemEntity(0, it.id, it.amount, it.date)

        // TODO: map products in purchase too
        // it.products
            is PurchaseHistoryItemModel -> PurchaseHistoryItemEntity(0, it.id, emptyList(), it.date)
            else -> throw IllegalArgumentException("No mapping function for '${it.javaClass}'")
        }
    }

    override fun persistListData(data: List<IdentifiableListItem>) {
        balancePersistenceManager.getStore().removeAll()
        purchasePersistenceManager.getStore().removeAll()

        balancePersistenceManager.getStore().put(data.filterByExpectedType())
        purchasePersistenceManager.getStore().put(data.filterByExpectedType())
    }

    override fun loadListDataFromPersistence(): List<IdentifiableListItem> {
        return listOf(balancePersistenceManager.getStore().all,
                purchasePersistenceManager.getStore().all).flatten()
    }

    private val optionsMenuComponent: OptionsMenuComponent by lazy {
        OptionsMenuComponent(this, optionsMenuRes = R.menu.options_menu_none,
                onCreateOptionsMenu = { menu: Menu?, menuInflater: MenuInflater? ->
                }, onOptionsMenuItemClicked = {
            false
        })
    }

    override fun initComponents(context: Context) {
        super.initComponents(context)
        optionsMenuComponent
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        optionsMenuComponent.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (super.onOptionsItemSelected(item)) {
            return true
        }
        return optionsMenuComponent.onOptionsItemSelected(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateBalance()
    }

    private fun updateBalance() {
        val currentBalance = -13.37

        account_balance.let {
            it.text = getString(R.string.account_balance, currentBalance)
            it.setTextColor(themeHelper.getBalanceColor(currentBalance))
        }

    }

}
