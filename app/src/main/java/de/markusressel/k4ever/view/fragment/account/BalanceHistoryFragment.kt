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
import android.view.*
import android.widget.CheckBox
import com.github.nitrico.lastadapter.LastAdapter
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic
import de.markusressel.k4ever.BR
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.account.*
import de.markusressel.k4ever.data.persistence.user.UserEntity_
import de.markusressel.k4ever.data.persistence.user.UserPersistenceManager
import de.markusressel.k4ever.databinding.ListItemBalanceHistoryItemBinding
import de.markusressel.k4ever.databinding.ListItemPurchaseHistoryItemBinding
import de.markusressel.k4ever.databinding.ListItemTransferHistoryItemBinding
import de.markusressel.k4ever.extensions.common.android.context
import de.markusressel.k4ever.extensions.common.filterByExpectedType
import de.markusressel.k4ever.extensions.data.toEntity
import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.rest.users.model.PurchaseHistoryItemModel
import de.markusressel.k4ever.rest.users.model.TransferHistoryItemModel
import de.markusressel.k4ever.view.activity.base.DetailActivityBase
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import de.markusressel.k4ever.view.fragment.account.transfer.TransferDetailActivity
import de.markusressel.k4ever.view.fragment.base.FabConfig
import de.markusressel.k4ever.view.fragment.base.MultiPersistableListFragmentBase
import io.reactivex.Single
import io.reactivex.functions.Function4
import kotlinx.android.synthetic.main.fragment__account__balance_history.*
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import kotlinx.android.synthetic.main.list_item__balance_history_item.view.*
import kotlinx.android.synthetic.main.list_item__purchase_history_item.view.*
import kotlinx.android.synthetic.main.list_item__transfer_history_item.view.*
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

    @Inject
    lateinit var tranferPersistenceManager: TransferHistoryItemPersistenceManager

    @Inject
    lateinit var userPersistenceManager: UserPersistenceManager

    override fun createAdapter(): LastAdapter {
        return LastAdapter(listValues,
                BR.item).map<BalanceHistoryItemEntity, ListItemBalanceHistoryItemBinding>(
                R.layout.list_item__balance_history_item) {
            onCreate {
                it.binding.presenter = this@BalanceHistoryFragment
            }
            onBind {
                onBind { holder ->
                    val balanceItem = holder.binding.item

                    val money_amount_balance = holder.binding.root.money_amount_balance

                    balanceItem?.let {
                        holder.binding.root.balance_history_item_description.text = if (balanceItem.amount < 0) {
                            getString(R.string.withdrawal)
                        } else {
                            getString(R.string.deposition)
                        }

                        money_amount_balance.text = getString(R.string.shopping_cart__item_cost,
                                balanceItem.amount)
                        money_amount_balance.setTextColor(
                                themeHandler.getBalanceColor(balanceItem.amount))
                    }
                }
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
                    onBind {
                        onBind { holder ->
                            val purchaseItem = holder.binding.item

                            purchaseItem?.let {
                                holder.binding.root.products.text = purchaseItem.products.size.toString()

                                val purchaseTotalCost = purchaseItem.products.map { it.price }.sum()
                                holder.binding.root.total_price.text = getString(
                                        R.string.shopping_cart__item_cost, purchaseTotalCost)
                                holder.binding.root.total_price.setTextColor(
                                        themeHandler.getBalanceColor(-1.0))
                            }
                        }
                    }
                    onClick {
                        // TODO: should there be any detail view of purchase history items?
                        //                        openDetailView(listValues[it.adapterPosition])
                    }
                }
                .map<TransferHistoryItemEntity, ListItemTransferHistoryItemBinding>(
                        R.layout.list_item__transfer_history_item) {
                    onCreate {
                        it.binding.presenter = this@BalanceHistoryFragment
                    }
                    onBind {
                        onBind { holder ->
                            val transferItem = holder.binding.item

                            transferItem?.let {
                                holder.binding.root.money_amount_transfer.text = getString(
                                        R.string.shopping_cart__item_cost, transferItem.amount)
                                holder.binding.root.icon_transfer.icon = if (transferItem.amount > 0) {
                                    iconHandler.getHistoryItemIcon(
                                            MaterialDesignIconic.Icon.gmi_chevron_down)
                                } else {
                                    iconHandler.getHistoryItemIcon(
                                            MaterialDesignIconic.Icon.gmi_chevron_up)
                                }

                                holder.binding.root.money_amount_transfer.text = getString(
                                        R.string.shopping_cart__item_cost, transferItem.amount)
                                holder.binding.root.money_amount_transfer.setTextColor(
                                        themeHandler.getBalanceColor(transferItem.amount))
                            }
                        }
                    }
                    onClick {
                        it.binding.item?.let {
                            openTransferDetailView(it)
                        }
                    }
                }.into(recyclerView)
    }

    override fun getLoadDataFromSourceFunction(): Single<List<Any>> {
        val currentUserId = 1L

        // download all data and wrap them in a new Single object
        return Single.zip(restClient.getAllUsers(), restClient.getBalanceHistory(currentUserId),
                restClient.getPurchaseHistory(currentUserId),
                restClient.getTransferHistory(currentUserId), Function4 { t1, t2, t3, t4 ->

            userPersistenceManager.getStore().removeAll()
            userPersistenceManager.getStore().put(t1.map { it.toEntity() })

            listOf(t2, t3, t4).flatten()
        })
    }

    override fun mapToEntity(it: Any): IdentifiableListItem {
        return when (it) {
            is BalanceHistoryItemModel -> BalanceHistoryItemEntity(0, it.id, it.amount, it.date)
            is TransferHistoryItemModel -> {
                val sender = userPersistenceManager.getStore().query().run {
                    equal(UserEntity_.id, it.sender.id)
                }.build().findUnique()
                val recipient = userPersistenceManager.getStore().query().run {
                    equal(UserEntity_.id, it.recipient.id)
                }.build().findUnique()

                val item = TransferHistoryItemEntity(id = it.id, amount = it.amount, date = it.date)
                item.sender.target = sender
                item.recipient.target = recipient

                item
            }
            is PurchaseHistoryItemModel -> PurchaseHistoryItemEntity(0, it.id,
                    it.products.map { it.toEntity() }, it.date)
            else -> throw IllegalArgumentException("No mapping function for '${it.javaClass}'")
        }
    }

    override fun persistListData(data: List<IdentifiableListItem>) {
        balancePersistenceManager.getStore().removeAll()
        purchasePersistenceManager.getStore().removeAll()
        tranferPersistenceManager.getStore().removeAll()

        balancePersistenceManager.getStore().put(data.filterByExpectedType())
        purchasePersistenceManager.getStore().put(data.filterByExpectedType())
        tranferPersistenceManager.getStore().put(data.filterByExpectedType())
    }

    override fun loadListDataFromPersistence(): List<IdentifiableListItem> {
        val transfers = tranferPersistenceManager.getStore().all
        val purchases = purchasePersistenceManager.getStore().all
        val balances = balancePersistenceManager.getStore().all
        return listOf<List<IdentifiableListItem>>(balances, purchases, transfers).flatten()
    }

    private val optionsMenuComponent: OptionsMenuComponent by lazy {
        OptionsMenuComponent(this, optionsMenuRes = R.menu.options_menu_filter,
                onCreateOptionsMenu = { menu: Menu?, menuInflater: MenuInflater? ->
                    val filterMenuItem = menu?.findItem(R.id.filter)
                    filterMenuItem?.icon = iconHandler.getOptionsMenuIcon(
                            MaterialDesignIconic.Icon.gmi_filter_list)
                }, onOptionsMenuItemClicked = {
            when {
                it.itemId == R.id.filter -> {
                    openFilterPopoupMenu(activity!!.findViewById(R.id.filter))
                    true
                }
                else -> false
            }
        })
    }

    private var showBalanceHistory by savedInstanceState(true)
    private var showPurchaseHistory by savedInstanceState(true)
    private var showTransferHistory by savedInstanceState(true)

    private fun openFilterPopoupMenu(anchorView: View) {
        val popupMenu = popupMenu {
            dropdownGravity = Gravity.BOTTOM
            section {
                customItem {
                    layoutResId = R.layout.view__popup_menu__checkbox
                    viewBoundCallback = { view ->
                        val checkBox: CheckBox = view.findViewById(R.id.popup_menu_checkbox)
                        checkBox.setText(R.string.show_deposit_withdrawals)
                        checkBox.isChecked = showBalanceHistory
                    }
                    callback = {
                        showBalanceHistory = !showBalanceHistory
                        updateListFromPersistence()
                    }
                }
                customItem {
                    layoutResId = R.layout.view__popup_menu__checkbox
                    viewBoundCallback = { view ->
                        val checkBox: CheckBox = view.findViewById(R.id.popup_menu_checkbox)
                        checkBox.setText(R.string.show_purchases)
                        checkBox.isChecked = showPurchaseHistory
                    }
                    callback = {
                        showPurchaseHistory = !showPurchaseHistory
                        updateListFromPersistence()
                    }
                }
                customItem {
                    layoutResId = R.layout.view__popup_menu__checkbox
                    viewBoundCallback = { view ->
                        val checkBox: CheckBox = view.findViewById(R.id.popup_menu_checkbox)
                        checkBox.setText(R.string.show_transfers)
                        checkBox.isChecked = showTransferHistory
                    }
                    callback = {
                        showTransferHistory = !showTransferHistory
                        updateListFromPersistence()
                    }

                }
            }
        }

        popupMenu.show(activity as Context, anchorView)
    }

    override fun filterListItem(item: IdentifiableListItem): Boolean {
        return when (item) {
            is BalanceHistoryItemEntity -> showBalanceHistory
            is TransferHistoryItemEntity -> showTransferHistory
            is PurchaseHistoryItemEntity -> showPurchaseHistory
            else -> true
        }
    }

    override fun sortListData(listData: List<IdentifiableListItem>): List<IdentifiableListItem> {
        val dateComparator = Comparator<IdentifiableListItem> { a, b ->
            val dateA = when (a) {
                is BalanceHistoryItemEntity -> a.date
                is PurchaseHistoryItemEntity -> a.date
                is TransferHistoryItemEntity -> a.date
                else -> throw IllegalArgumentException(
                        "Cant compare object of type ${a.javaClass}!")
            }

            val dateB = when (b) {
                is BalanceHistoryItemEntity -> b.date
                is PurchaseHistoryItemEntity -> b.date
                is TransferHistoryItemEntity -> b.date
                else -> throw IllegalArgumentException(
                        "Cant compare object of type ${b.javaClass}!")
            }

            dateA.compareTo(dateB)
        }

        return listData.sortedWith(dateComparator)
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
            it.setTextColor(themeHandler.getBalanceColor(currentBalance))
        }

    }

    private fun openTransferDetailView(item: TransferHistoryItemEntity) {
        val detailPage = DetailActivityBase.newInstanceIntent(TransferDetailActivity::class.java,
                context(), item.entityId)
        startActivity(detailPage)
    }

}
