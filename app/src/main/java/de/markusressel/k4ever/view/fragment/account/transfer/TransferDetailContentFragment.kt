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

import android.os.Bundle
import android.view.View
import de.markusressel.k4ever.R
import de.markusressel.k4ever.dagger.module.Implementation
import de.markusressel.k4ever.dagger.module.ImplementationTypeEnum
import de.markusressel.k4ever.data.persistence.account.TransferHistoryItemEntity
import de.markusressel.k4ever.data.persistence.account.TransferHistoryItemPersistenceManager
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.user.UserEntity
import de.markusressel.k4ever.rest.K4EverRestApiClient
import de.markusressel.k4ever.view.fragment.base.DetailContentFragmentBase
import kotlinx.android.synthetic.main.layout__item_detail__transfer.*
import javax.inject.Inject


class TransferDetailContentFragment : DetailContentFragmentBase<TransferHistoryItemEntity>() {

    override val layoutRes: Int
        get() = R.layout.layout__item_detail__transfer

    @Inject
    protected lateinit var persistenceManager: TransferHistoryItemPersistenceManager

    override fun getPersistenceHandler(): PersistenceManagerBase<TransferHistoryItemEntity> = persistenceManager

    @Inject
    @field:Implementation(ImplementationTypeEnum.DUMMY)
    lateinit var restClient: K4EverRestApiClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val entity = getEntityFromPersistence()
        transferSender.text = entity.sender.target.display_name
        transferRecipient.text = entity.recipient.target.display_name
        transferAmount.text = getString(R.string.shopping_cart__item_cost, entity.amount)

        sender_avatar.setImageURI(getAvatarUrl(entity.sender.target))
        recipient_avatar.setImageURI(getAvatarUrl(entity.recipient.target))
    }

    private fun getAvatarUrl(user: UserEntity): String {
        return restClient.getUserAvatarURL(user.id)
    }

}