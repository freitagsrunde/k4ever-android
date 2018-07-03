package de.markusressel.k4ever.data.persistence.account

import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceHistoryItemPersistenceManager @Inject constructor() :
        PersistenceManagerBase<BalanceHistoryItemEntity>(BalanceHistoryItemEntity::class)