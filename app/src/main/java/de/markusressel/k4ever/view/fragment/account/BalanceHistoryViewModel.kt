package de.markusressel.k4ever.view.fragment.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.markusressel.k4ever.data.persistence.IdentifiableListItem
import de.markusressel.k4ever.data.persistence.account.*
import de.markusressel.k4ever.view.EntityListViewModel
import io.objectbox.android.ObjectBoxDataSource
import io.objectbox.android.ObjectBoxLiveData
import io.objectbox.kotlin.query
import io.objectbox.query.Query


class BalanceHistoryViewModel(balanceHistoryItemPersistenceManager: BalanceHistoryItemPersistenceManager,
                              purchaseHistoryItemPersistenceManager: PurchaseHistoryItemPersistenceManager,
                              transferHistoryItemPersistenceManager: TransferHistoryItemPersistenceManager) : EntityListViewModel() {


    var entityLiveData = zipLiveData(
            createLiveData(balanceHistoryItemPersistenceManager.getStore().query { orderDesc(BalanceHistoryItemEntity_.date) }),
            createLiveData(purchaseHistoryItemPersistenceManager.getStore().query { orderDesc(PurchaseHistoryItemEntity_.date) }),
            createLiveData(transferHistoryItemPersistenceManager.getStore().query { orderDesc(TransferHistoryItemEntity_.date) })
    )

    private fun zipLiveData(vararg liveItems: LiveData<*>): LiveData<List<IdentifiableListItem>> {
        return MediatorLiveData<List<IdentifiableListItem>>().apply {
            val lock = Object()
            var zippedObjects: MutableList<IdentifiableListItem> = ArrayList()
            liveItems.forEach {
                addSource(it) {
                    synchronized(lock) {
                        val items = it as List<IdentifiableListItem>
                        if (items.isNotEmpty()) {
                            val itemType = items[0].javaClass
                            zippedObjects = zippedObjects.filterNot { it.javaClass == itemType }.toMutableList()
                            zippedObjects.addAll(items)
                        }
                    }
                    value = zippedObjects
                }
            }
        }
    }

    private fun <X : Any> createLiveData(query: Query<X>): LiveData<List<X>> {
        return ObjectBoxLiveData(query)
    }

    private fun <X : Any> createPaged(query: Query<X>): LiveData<PagedList<X>> {
        // paging is afaik currently not possible with MediatorLiveData
        return LivePagedListBuilder(ObjectBoxDataSource.Factory(query), getPageSize()).build()
    }

}