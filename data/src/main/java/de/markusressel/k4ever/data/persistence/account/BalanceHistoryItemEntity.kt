package de.markusressel.k4ever.data.persistence.account

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class BalanceHistoryItemEntity(@Id var entityId: Long = 0, val id: Long, val amount: Double, val date: Date)