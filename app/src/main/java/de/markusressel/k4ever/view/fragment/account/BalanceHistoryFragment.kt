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
import de.markusressel.k4ever.data.persistence.account.BalanceHistoryItemEntity
import de.markusressel.k4ever.data.persistence.account.BalanceHistoryItemPersistenceManager
import de.markusressel.k4ever.data.persistence.account.PurchaseHistoryItemEntity
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.databinding.ListItemBalanceHistoryItemBinding
import de.markusressel.k4ever.databinding.ListItemPurchaseHistoryItemBinding
import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import de.markusressel.k4ever.view.fragment.base.FabConfig
import de.markusressel.k4ever.view.fragment.base.PersistableListFragmentBase
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment__account__balance_history.*
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import java.util.*
import javax.inject.Inject


/**
 * Server Status fragment
 *
 * Created by Markus on 07.01.2018.
 */
class BalanceHistoryFragment : PersistableListFragmentBase<BalanceHistoryItemModel, BalanceHistoryItemEntity>() {

    override val layoutRes: Int
        get() = R.layout.fragment__account__balance_history

    override fun getLeftFabs(): List<FabConfig.Fab> {
        return listOf(FabConfig.Fab(description = R.string.withdraw_money, icon = MaterialDesignIconic.Icon.gmi_minus, onClick = {
            // TODO: open "withdraw money" dialog
        }), FabConfig.Fab(description = R.string.deposit_money, icon = MaterialDesignIconic.Icon.gmi_plus, onClick = {
            // TODO: open "deposit money" dialog
        }))
    }

    override fun getRightFabs(): List<FabConfig.Fab> {
        return listOf()
    }

    @Inject
    lateinit var persistenceManager: BalanceHistoryItemPersistenceManager

    override fun getPersistenceHandler(): PersistenceManagerBase<BalanceHistoryItemEntity> = persistenceManager

    override fun createAdapter(): LastAdapter {
        return LastAdapter(listValues, BR.item).map<BalanceHistoryItemEntity, ListItemBalanceHistoryItemBinding>(R.layout.list_item__balance_history_item) {
            onCreate {
                it.binding.presenter = this@BalanceHistoryFragment
            }
            onClick {
                // TODO: should there be any detail view of balance history items?
//                        openDetailView(listValues[it.adapterPosition])
            }
        }.map<PurchaseHistoryItemEntity, ListItemPurchaseHistoryItemBinding>(R.layout.list_item__purchase_history_item) {
                    onCreate {
                        it.binding.presenter = this@BalanceHistoryFragment
                    }
                    onClick {
                        // TODO: should there be any detail view of purchase history items?
//                        openDetailView(listValues[it.adapterPosition])
                    }
                }.into(recyclerView)
    }

    override fun mapToEntity(it: BalanceHistoryItemModel): BalanceHistoryItemEntity {
        return BalanceHistoryItemEntity(id = it.id, amount = it.amount, date = it.date)
    }

    override fun loadListDataFromSource(): Single<List<BalanceHistoryItemModel>> {
        val h1 = BalanceHistoryItemModel(0, 5.0, Date())
        val h2 = BalanceHistoryItemModel(0, 5.0, Date())
        val h3 = BalanceHistoryItemModel(0, 5.0, Date())

        return Single.just(listOf(h1, h2, h3))
    }

    private val optionsMenuComponent: OptionsMenuComponent by lazy {
        OptionsMenuComponent(this, optionsMenuRes = R.menu.options_menu_none, onCreateOptionsMenu = { menu: Menu?, menuInflater: MenuInflater? ->
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
