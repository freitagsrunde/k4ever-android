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

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import com.github.ajalt.timberkt.Timber
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.SearchableListItem
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit


/**
 * Created by Markus on 29.01.2018.
 */
abstract class PersistableListFragmentBase<ModelType : Any, EntityType> : ListFragmentBase() where EntityType : IdentifiableListItem {

    protected val listValues: MutableList<EntityType> = ArrayList()

    private val optionsMenuComponent: OptionsMenuComponent by lazy {
        OptionsMenuComponent(hostFragment = this, optionsMenuRes = R.menu.options_menu_list,
                onCreateOptionsMenu = { menu: Menu?, menuInflater: MenuInflater? ->
                    val sortOptionMenuItem = menu?.findItem(R.id.sortOrder)
                    sortOptionMenuItem?.let {
                        it.icon = iconHandler.getOptionsMenuIcon(MaterialDesignIconic.Icon.gmi_sort)

                        if (getAllSortCriteria().isEmpty()) {
                            sortOptionMenuItem.isVisible = false
                        }
                    }

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
            when {
                it.itemId == R.id.sortOrder -> {
                    openSortSelection()
                    true
                }
                else -> false
            }
        })
    }

    override fun initComponents(context: Context) {
        super.initComponents(context)
        optionsMenuComponent
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        optionsMenuComponent.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (super.onOptionsItemSelected(item)) {
            return true
        }
        return optionsMenuComponent.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(
                        5) > getLastUpdatedFromSource()) {
            Timber.d { "Persisted list data is old, refreshing from source" }
            reloadDataFromSource()
        } else {
            Timber.d { "Persisted list data is probably still valid, just loading from persistence" }
            updateListFromPersistence()
        }
    }

    /**
     * Loads the data using {@link loadListDataFromPersistence()}
     */
    protected fun updateListFromPersistence() {
        setRefreshing(true)

        Observable.fromIterable(loadListDataFromPersistence()).toList()
                .map { filterAndSortList(it) }.map {
                    createListDiff(listValues, it) to it
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .bindUntilEvent(this, Lifecycle.Event.ON_STOP).subscribeBy(onSuccess = {
                    updateAdapterList(it.first, it.second)
                    scrollToItemPosition(lastScrollPosition)
                }, onError = {
                    if (it is CancellationException) {
                        Timber.d { "reload from persistence cancelled" }
                    } else {
                        loadingComponent.showError(it)
                        setRefreshing(false)
                    }
                })
    }

    /**
     * Creates a diff of two lists
     */
    protected fun createListDiff(oldData: List<EntityType>,
                                 newData: List<EntityType>): DiffUtil.DiffResult {
        val diffCallback = DiffCallback(oldData, newData)
        return DiffUtil.calculateDiff(diffCallback)
    }

    /**
     * Updates the content of the adapter behind the recyclerview
     */
    protected fun updateAdapterList(diffResult: DiffUtil.DiffResult, newData: List<EntityType>) {
        listValues.clear()
        listValues.addAll(newData)

        if (listValues.isEmpty()) {
            showEmpty()
        } else {
            hideEmpty()
        }
        setRefreshing(false)

        diffResult.dispatchUpdatesTo(recyclerViewAdapter)
        recyclerViewAdapter.notifyDataSetChanged()
        recyclerView.invalidate()
    }

    /**
     * Filters and sorts the given list by the currently active filter and sort options
     * Remember to call this before using {@link updateAdapterList }
     */
    private fun filterAndSortList(newData: List<EntityType>): List<EntityType> {
        val filteredNewData = newData.filter {
            currentSearchFilter.isEmpty() || itemContainsCurrentSearchString(it)
        }.toList()
        return sortByCurrentOptions(filteredNewData)
    }

    private fun itemContainsCurrentSearchString(item: EntityType): Boolean {
        return when (item) {
            is SearchableListItem -> item.getSearchableContent().any {
                it.toString().contains(currentSearchFilter, true)
            }
            else -> item.toString().contains(currentSearchFilter, true)
        }
    }

    /**
     * Sorts a list by the currently selected SortOptions
     */
    private fun sortByCurrentOptions(listData: List<EntityType>): List<EntityType> {
        val sortOptions = getCurrentSortOptions()

        if (sortOptions.isEmpty()) {
            return listData
        }

        // create initial comparator
        var comparator: Comparator<EntityType> = if (sortOptions.first().reversed) {
            compareByDescending(sortOptions.first().selector)
        } else {
            compareBy(sortOptions.first().selector)
        }

        // extend it with other criteria
        sortOptions.drop(1).forEach { criteria ->
            comparator = if (criteria.reversed) {
                comparator.thenByDescending(criteria.selector)
            } else {
                comparator.thenBy(criteria.selector)
            }
        }

        return listData.sortedWith(comparator)
    }

    /**
     * Returns a list of all available sort criteria
     * Override this method in child classes
     */
    open fun getAllSortCriteria(): List<SortOption<EntityType>> {
        return emptyList()
    }

    /**
     * Get a list of the currently selected (active) sort criteria
     */
    open fun getCurrentSortOptions(): List<SortOption<EntityType>> {
        // TODO:
        return getAllSortCriteria()
    }

    private fun openSortSelection() {
        // TODO:
    }

    /**
     * Reload list data asEntity it's original source, persist it and display it to the user afterwards
     */
    override fun reloadDataFromSource() {
        setRefreshing(true)

        loadListDataFromSource().map {
            it.map { mapToEntity(it) }
        }.map {
            persistListData(it)
            it
        }.map {
            filterAndSortList(it)
        }.map {
            createListDiff(listValues, it) to it
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .bindUntilEvent(this, Lifecycle.Event.ON_STOP).subscribeBy(onSuccess = {
                    updateLastUpdatedFromSource()
                    updateAdapterList(it.first, it.second)
                    scrollToItemPosition(lastScrollPosition)
                }, onError = {
                    setRefreshing(false)

                    if (it is CancellationException) {
                        Timber.d { "reload from source cancelled" }
                    } else {
                        loadingComponent.showError(it)
                    }
                })
    }

    /**
     * Map the source object to the persistence object
     */
    abstract fun mapToEntity(it: ModelType): EntityType

    /**
     * Get the persistence handler for this list
     */
    protected abstract fun getPersistenceHandler(): PersistenceManagerBase<EntityType>

    private fun persistListData(data: List<EntityType>) {
        getPersistenceHandler().getStore().removeAll()
        getPersistenceHandler().getStore().put(data)
    }

    private fun getLastUpdatedFromSource(): Long {
        // TODO:
        val entityModelId = getPersistenceHandler().getEntityModelId()
        //        return lastUpdatedManager
        //                .getLastUpdated(entityModelId.toLong())
        return 0
    }

    private fun updateLastUpdatedFromSource() {
        // TODO:
        val entityModelId = getPersistenceHandler().getEntityModelId()
        //        lastUpdatedManager
        //                .setUpdatedNow(entityModelId.toLong())
    }

    /**
     * Load the data to be displayed in the list asEntity the persistence
     */
    open fun loadListDataFromPersistence(): List<EntityType> {
        val persistenceHandler = getPersistenceHandler()
        return persistenceHandler.getStore().all
    }

    /**
     * Load the data to be displayed in the list from it's original source
     */
    abstract fun loadListDataFromSource(): Single<List<ModelType>>

}
