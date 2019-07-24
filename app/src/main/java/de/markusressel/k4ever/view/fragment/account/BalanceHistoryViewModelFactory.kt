package de.markusressel.k4ever.view.fragment.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.markusressel.k4ever.data.persistence.account.BalanceHistoryItemPersistenceManager
import de.markusressel.k4ever.data.persistence.account.PurchaseHistoryItemPersistenceManager
import de.markusressel.k4ever.data.persistence.account.TransferHistoryItemPersistenceManager

class BalanceHistoryViewModelFactory(val balanceHistoryItemPersistenceManager: BalanceHistoryItemPersistenceManager,
                                     val purchaseHistoryItemPersistenceManager: PurchaseHistoryItemPersistenceManager,
                                     val transferHistoryItemPersistenceManager: TransferHistoryItemPersistenceManager) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BalanceHistoryViewModel(balanceHistoryItemPersistenceManager, purchaseHistoryItemPersistenceManager, transferHistoryItemPersistenceManager) as T
    }
}