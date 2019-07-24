package de.markusressel.k4ever.view

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import io.objectbox.android.ObjectBoxDataSource
import io.objectbox.query.Query

/**
 * Base class for implementing a ViewModel for item lists
 */
abstract class TypedEntityListViewModel<EntityType : Any> : EntityListViewModel() {

    private var listLiveData: LiveData<PagedList<EntityType>>? = null

    /**
     * Get the LiveData object for this EntityListViewModel
     */
    fun getListLiveData(persistenceManager: PersistenceManagerBase<EntityType>): LiveData<PagedList<EntityType>> {
        if (listLiveData == null) {
            listLiveData = LivePagedListBuilder(ObjectBoxDataSource.Factory(createDbQuery(persistenceManager)), getPageSize()).build()
        }

        return listLiveData!!
    }

    /**
     * Define the query to use for the list data of this ViewModel
     */
    abstract fun createDbQuery(persistenceManager: PersistenceManagerBase<EntityType>): Query<EntityType>

}