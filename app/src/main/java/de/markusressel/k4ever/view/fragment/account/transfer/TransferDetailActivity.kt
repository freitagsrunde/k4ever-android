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

package de.markusressel.k4ever.view.fragment.account.transfer

import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.account.TransferHistoryItemEntity
import de.markusressel.k4ever.data.persistence.account.TransferHistoryItemPersistenceManager
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.view.activity.base.DetailActivityBase
import de.markusressel.k4ever.view.fragment.base.DaggerSupportFragmentBase
import javax.inject.Inject

/**
 * Product detail page
 *
 * Created by Markus on 07.01.2018.
 */
class TransferDetailActivity : DetailActivityBase<TransferHistoryItemEntity>() {

    @Inject
    protected lateinit var persistenceManager: TransferHistoryItemPersistenceManager

    override val headerTextString: String
        get() = getItem().recipient.target.display_name

    override val tabItems: List<Pair<Int, () -> DaggerSupportFragmentBase>>
        get() = listOf(R.string.details to ::TransferDetailContentFragment)

    override fun getPersistenceHandler(): PersistenceManagerBase<TransferHistoryItemEntity> = persistenceManager

}
