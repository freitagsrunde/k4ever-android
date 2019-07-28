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
import com.github.ajalt.timberkt.Timber
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.SearchableListItem
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit


/**
 * Created by Markus on 29.01.2018.
 */
abstract class MultiPersistableListFragmentBase : ListFragmentBase() {

    val pagedEpoxyController: EpoxyController by lazy { createEpoxyController() }
    override fun getEpoxyController(): EpoxyController = pagedEpoxyController

    /**
     * Create the epoxy controller here.
     * The epoxy controller defines what information is displayed.
     */
    abstract fun createEpoxyController(): EpoxyController

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

                                    // TODO: implement search
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
        optionsMenuComponent
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        optionsMenuComponent.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item) || optionsMenuComponent.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reloadDataFromSource()
    }

    /**
     * Filter list items that don't match this function from the visible list
     * @return true, if the item is ok, false if it should be filtered
     */
    internal open fun filterListItem(item: IdentifiableListItem): Boolean {
        return true
    }

    private fun itemContainsCurrentSearchString(item: IdentifiableListItem): Boolean {
        return when (item) {
            is SearchableListItem -> item.getSearchableContent().any {
                it.toString().contains(currentSearchFilter, true)
            }
            else -> item.toString().contains(currentSearchFilter, true)
        }
    }

    /**
     * Filters and sorts the given list by the currently active filter and sort options
     * Remember to call this before using {@link updateAdapterList }
     */
    private fun filterAndSortList(newData: List<IdentifiableListItem>): List<IdentifiableListItem> {
        // TODO: do this in viewmodel instead
        val filteredNewData = newData.filter {
            currentSearchFilter.isEmpty() || itemContainsCurrentSearchString(it)
        }.filter { filterListItem(it) }.toList()
        return sortListData(filteredNewData)
    }

    /**
     * Sorts a list by the currently selected SortOptions
     */
    abstract fun sortListData(listData: List<IdentifiableListItem>): List<IdentifiableListItem>

    /**
     * Reload list data from it's original source, persist it and display it to the user afterwards
     */
    override fun reloadDataFromSource() {
        setRefreshing(true)

        try {
            runBlocking(Dispatchers.IO) {
                val sourceData = getDataFromSource().map {
                    mapToEntity(it)
                }

                persistListData(sourceData)
                updateLastUpdatedFromSource()
            }
        } catch (ex: Exception) {
            if (ex is CancellationException) {
                Timber.d { "reload from source cancelled" }
            } else {
                loadingComponent.showError(ex)
            }
        } finally {
            setRefreshing(false)
        }
    }

    /**
     * Define a Single that returns the complete list of data from the (server) source
     */
    internal abstract suspend fun getDataFromSource(): List<Any>

    /**
     * Map the source object to the persistence object
     */
    abstract fun mapToEntity(it: Any): IdentifiableListItem

    /**
     * Persist the current list data
     */
    internal abstract fun persistListData(data: List<IdentifiableListItem>)

    private fun getLastUpdatedFromSource(): Long {
        // TODO:
        return 0
    }

    private fun updateLastUpdatedFromSource() {
        // TODO:
    }

}
