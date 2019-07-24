package de.markusressel.k4ever.view.fragment.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.markusressel.k4ever.data.persistence.account.BalanceHistoryItemPersistenceManager
import de.markusressel.k4ever.data.persistence.account.PurchaseHistoryItemPersistenceManager
import de.markusressel.k4ever.data.persistence.account.TransferHistoryItemPersistenceManager
import de.markusressel.k4ever.view.EntityListViewModel
import io.objectbox.android.ObjectBoxDataSource
import io.objectbox.kotlin.query
import io.objectbox.query.Query

class BalanceHistoryViewModel(val balanceHistoryItemPersistenceManager: BalanceHistoryItemPersistenceManager,
                              val purchaseHistoryItemPersistenceManager: PurchaseHistoryItemPersistenceManager,
                              val transferHistoryItemPersistenceManager: TransferHistoryItemPersistenceManager) : EntityListViewModel() {


    var entityLiveData = zipLiveData(
            createPaged(balanceHistoryItemPersistenceManager.getStore().query { }),
            createPaged(purchaseHistoryItemPersistenceManager.getStore().query { }),
            createPaged(transferHistoryItemPersistenceManager.getStore().query { })
    )

    private fun zipLiveData(vararg liveItems: LiveData<*>): LiveData<List<Any>> {
        return MediatorLiveData<List<Any>>().apply {
            val zippedObjects = ArrayList<Any>()
            liveItems.forEach {
                addSource(it) { item ->
                    if (!zippedObjects.contains(item as Any)) {
                        zippedObjects.add(item)
                    }
                    value = zippedObjects
                }
            }
        }
    }

//    fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
//        return MediatorLiveData<Pair<A, B>>().apply {
//            var lastA: A? = null
//            var lastB: B? = null
//
//            fun update() {
//                val localLastA = lastA
//                val localLastB = lastB
//                if (localLastA != null && localLastB != null)
//                    this.value = Pair(localLastA, localLastB)
//            }
//
//            addSource(a) {
//                lastA = it
//                update()
//            }
//            addSource(b) {
//                lastB = it
//                update()
//            }
//        }
//    }

//    /**
//     * Get the LiveData object for this EntityViewModel
//     */
//    fun getEntityLiveData(persistenceManager: PersistenceManagerBase<BalanceHistoryItemEntity>): LiveData<PagedList<Any>> {
//        val balanceItems = createPaged(persistenceManager.getStore().query { })
//        entityLiveData.addSource(balanceItems) {
//            entityLiveData.value = it as PagedList<Any>
//        }
//
//        return entityLiveData
//    }

    private fun <X : Any> createPaged(query: Query<X>): LiveData<PagedList<X>> {
        return LivePagedListBuilder(ObjectBoxDataSource.Factory(query), getPageSize()).build()
    }

}