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

package de.markusressel.k4ever.view.fragment.base

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.util.DiffUtil
import android.support.v7.widget.SearchView
import android.view.*
import com.github.ajalt.timberkt.Timber
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.SearchableListItem
import de.markusressel.k4ever.view.component.LoadingComponent
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import kotlinx.android.synthetic.main.layout_empty_list.*
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by Markus on 29.01.2018.
 */
abstract class MultiPersistableListFragmentBase : ListFragmentBase() {

    protected val listValues: MutableList<IdentifiableListItem> = ArrayList()

    protected val loadingComponent by lazy {
        LoadingComponent(this, onShowContent = {
            updateFabVisibility(View.VISIBLE)
        }, onShowError = { message: String, throwable: Throwable? ->
            layoutEmpty.visibility = View.GONE
            updateFabVisibility(View.INVISIBLE)
        })
    }

    private val optionsMenuComponent: OptionsMenuComponent by lazy {
        OptionsMenuComponent(hostFragment = this, optionsMenuRes = R.menu.options_menu_multi_list,
                onCreateOptionsMenu = { menu: Menu?, menuInflater: MenuInflater? ->
                    val searchMenuItem = menu?.findItem(R.id.search)
                    searchMenuItem?.icon = iconHandler.getOptionsMenuIcon(
                            MaterialDesignIconic.Icon.gmi_search)

                    val searchView = searchMenuItem?.actionView as SearchView?
                    searchView?.let {
                        RxSearchView.queryTextChanges(it).skipInitialValue()
                                .bindUntilEvent(this, Lifecycle.Event.ON_DESTROY)
                                .debounce(300, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(onNext = {
                                    currentSearchFilter = it.toString()
                                    updateListFromPersistence()
                                }, onError = {
                                    Timber.e(it) { "Error filtering list" }
                                })
                    }

                }, onOptionsMenuItemClicked = {
            false
        })
    }

    override fun initComponents(context: Context) {
        super.initComponents(context)
        loadingComponent
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val parent = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        return loadingComponent.onCreateView(inflater, parent, savedInstanceState)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener {
            reloadDataFromSource()
        }
    }

    override fun onResume() {
        super.onResume()

        if (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(
                        5) > getLastUpdatedFromSource()) {
            Timber.d { "Persisted list data is old, refreshing from source" }
            reloadDataFromSource()
        } else {
            Timber.d { "Persisted list data is probably still valid, just loading from persistence" }
            updateListFromPersistence()
        }

        updateListFromPersistence()
    }

    /**
     * Loads the data using {@link loadListDataFromPersistence()}
     */
    protected fun updateListFromPersistence() {
        loadingComponent.showLoading()

        Observable.fromIterable(loadListDataFromPersistence()).filter {
            currentSearchFilter.isEmpty() || itemContainsCurrentSearchString(it)
        }.filter { filterListItem(it) }.toList().map { sortListData(it) }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .bindUntilEvent(this, Lifecycle.Event.ON_STOP).subscribeBy(onSuccess = {
                    updateAdapterList(it)
                }, onError = {
                    if (it is CancellationException) {
                        Timber.d { "reload from persistence cancelled" }
                    } else {
                        loadingComponent.showError(it)
                    }
                })
    }

    /**
     * Filter list items that don't match this function from the visible list
     * @return true, if the item is ok, false if it should be filtered
     */
    open internal fun filterListItem(item: IdentifiableListItem): Boolean {
        return true
    }

    private fun itemContainsCurrentSearchString(item: IdentifiableListItem): Boolean {
        return when (item) {
            is SearchableListItem -> item.getSearchableContent().any {
                item.toString().contains(currentSearchFilter, true)
            }
            else -> item.toString().contains(currentSearchFilter, true)
        }
    }

    protected fun updateAdapterList(newData: List<IdentifiableListItem>) {
        val diffCallback = DiffCallback(listValues, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        listValues.clear()
        listValues.addAll(newData)

        if (listValues.isEmpty()) {
            showEmpty()
        } else {
            hideEmpty()
        }
        loadingComponent.showContent()

        diffResult.dispatchUpdatesTo(recyclerViewAdapter)
    }

    /**
     * Sorts a list by the currently selected SortOptions
     */
    abstract fun sortListData(listData: List<IdentifiableListItem>): List<IdentifiableListItem>

    /**
     * Reload list data from it's original source, persist it and display it to the user afterwards
     */
    protected open fun reloadDataFromSource() {
        loadingComponent.showLoading()

        getLoadDataFromSourceFunction().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .bindUntilEvent(this, Lifecycle.Event.ON_STOP).subscribeBy(onSuccess = {
                    val entities = it.map {
                        mapToEntity(it)
                    }

                    persistListData(entities)
                    updateLastUpdatedFromSource()
                    updateListFromPersistence()

                    swipeRefreshLayout.isRefreshing = false
                }, onError = {
                    swipeRefreshLayout.isRefreshing = false

                    if (it is CancellationException) {
                        Timber.d { "reload from source cancelled" }
                    } else {
                        loadingComponent.showError(it)
                    }
                })
    }

    /**
     * Define a Single that returns the complete list of data from the (server) source
     */
    abstract internal fun getLoadDataFromSourceFunction(): Single<List<Any>>

    /**
     * Map the source object to the persistence object
     */
    abstract fun mapToEntity(it: Any): IdentifiableListItem

    /**
     * Persist the current list data
     */
    abstract internal fun persistListData(data: List<IdentifiableListItem>)

    private fun getLastUpdatedFromSource(): Long {
        // TODO:
        return 0
    }

    private fun updateLastUpdatedFromSource() {
        // TODO:
    }

    /**
     * Load the data to be displayed in the list from the persistence
     */
    abstract fun loadListDataFromPersistence(): List<IdentifiableListItem>

    companion object {
        private val loaderIdCounter: AtomicInteger = AtomicInteger()
    }

}
