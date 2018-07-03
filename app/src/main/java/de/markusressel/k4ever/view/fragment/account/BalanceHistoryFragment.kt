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
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.databinding.ListItemBalanceHistoryItemBinding
import de.markusressel.k4ever.rest.users.model.BalanceHistoryItemModel
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import de.markusressel.k4ever.view.fragment.base.FabConfig
import de.markusressel.k4ever.view.fragment.base.ListFragmentBase
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import java.util.*
import javax.inject.Inject


/**
 * Server Status fragment
 *
 * Created by Markus on 07.01.2018.
 */
class BalanceHistoryFragment : ListFragmentBase<BalanceHistoryItemModel, BalanceHistoryItemEntity>() {

    override val layoutRes: Int
        get() = R.layout.fragment__account__balance_history

    override fun getLeftFabs(): List<FabConfig.Fab> {
        return listOf(FabConfig.Fab(description = R.string.withdraw_money, icon = MaterialDesignIconic.Icon.gmi_minus, onClick = {
            // TODO: open "withdraw money" dialog
        }))
    }

    override fun getRightFabs(): List<FabConfig.Fab> {
        return listOf(FabConfig.Fab(description = R.string.deposit_money, icon = MaterialDesignIconic.Icon.gmi_plus, onClick = {
            // TODO: open "deposit money" dialog
        }))
    }

    @Inject
    lateinit var persistenceManager: BalanceHistoryItemPersistenceManager

    override fun getPersistenceHandler(): PersistenceManagerBase<BalanceHistoryItemEntity> = persistenceManager

    override fun createAdapter(): LastAdapter {
        return LastAdapter(listValues, BR.item)
                .map<BalanceHistoryItemEntity, ListItemBalanceHistoryItemBinding>(R.layout.list_item__balance_history_item) {
                    onCreate {
                        it
                                .binding
                                .presenter = this@BalanceHistoryFragment
                    }
                    onClick {
                        // TODO: should there be any detail view of balance history items?
//                        openDetailView(listValues[it.adapterPosition])
                    }
                }
                .into(recyclerView)
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
        super
                .initComponents(context)
        optionsMenuComponent
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super
                .onCreateOptionsMenu(menu, inflater)
        optionsMenuComponent
                .onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (super.onOptionsItemSelected(item)) {
            return true
        }
        return optionsMenuComponent
                .onOptionsItemSelected(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super
                .onViewCreated(view, savedInstanceState)
    }

}
