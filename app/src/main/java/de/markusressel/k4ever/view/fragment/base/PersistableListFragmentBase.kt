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
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.github.ajalt.timberkt.Timber
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.SearchableListItem
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Markus on 29.01.2018.
 */
abstract class PersistableListFragmentBase<ModelType : Any, EntityType> : ListFragmentBase() where EntityType : IdentifiableListItem {

    val epoxyController: PagedListEpoxyController<EntityType> by lazy { createEpoxyController() }
    override fun getEpoxyController(): EpoxyController = epoxyController

    /**
     * Create the epoxy controller here.
     * The epoxy controller defines what information is displayed.
     */
    abstract fun createEpoxyController(): PagedListEpoxyController<EntityType>

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

                                    // TODO: implement search
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
        }
    }

    /**
     * Filters and sorts the given list by the currently active filter and sort options
     * Remember to call this before using {@link updateAdapterList }
     */
    private fun filterAndSortList(newData: List<EntityType>): List<EntityType> {
        // TODO: do this in viewmodel
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

        try {
            val handler = CoroutineExceptionHandler { context, exception ->
                loadingComponent.showError(exception)
            }

            CoroutineScope(Dispatchers.Main).launch(handler) {
                withContext(Dispatchers.IO) {
                    val newData = loadListDataFromSource().map {
                        mapToEntity(it)
                    }
                    persistListData(newData)
                    updateLastUpdatedFromSource()
                }
                setRefreshing(false)
            }
        } catch (ex: Exception) {
            loadingComponent.showError(ex)
            setRefreshing(false)
        }
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
        // TODO: remove missing items
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
        return getPersistenceHandler().getStore().all
    }

    /**
     * Load the data to be displayed in the list from it's original source
     */
    abstract suspend fun loadListDataFromSource(): List<ModelType>

}
