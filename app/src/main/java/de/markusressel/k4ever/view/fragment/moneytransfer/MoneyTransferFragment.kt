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

package de.markusressel.k4ever.view.fragment.moneytransfer

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.View
import com.github.ajalt.timberkt.Timber
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import de.markusressel.k4ever.R
import de.markusressel.k4ever.dagger.module.Implementation
import de.markusressel.k4ever.dagger.module.ImplementationTypeEnum
import de.markusressel.k4ever.data.persistence.user.UserEntity
import de.markusressel.k4ever.data.persistence.user.UserPersistenceManager
import de.markusressel.k4ever.extensions.common.android.context
import de.markusressel.k4ever.extensions.common.android.gui.toast
import de.markusressel.k4ever.extensions.data.toEntity
import de.markusressel.k4ever.rest.K4EverRestApiClient
import de.markusressel.k4ever.view.fragment.base.DaggerDialogFragmentBase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment__money_transfer.*
import java.util.concurrent.CancellationException
import javax.inject.Inject


/**
 * Server Status fragment
 *
 * Created by Markus on 07.01.2018.
 */
class MoneyTransferFragment : DaggerDialogFragmentBase() {

    override val layoutRes: Int
        get() = R.layout.fragment__money_transfer

    @Inject
    lateinit var userPersistenceManager: UserPersistenceManager

    @Inject
    @field:Implementation(ImplementationTypeEnum.DUMMY)
    lateinit var restClient: K4EverRestApiClient

    private lateinit var autocompleteUsersArrayAdapter: UserArrayAdapter
    private var currentRecipientUserId: Long? by savedInstanceState()
    private var savedValue by savedInstanceState(0)
    private var currentDescription by savedInstanceState("")

    @SuppressLint("ClickableViewAccessibility")
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        autocompleteUsersArrayAdapter = UserArrayAdapter(context(), restClient)
        recipient_searchview.setAdapter(autocompleteUsersArrayAdapter)
        recipient_searchview.threshold = 1
        recipient_searchview.setOnItemClickListener { parent, view, position, id ->
            setSelectedUser(autocompleteUsersArrayAdapter.getItem(position))
        }
        recipient_searchview.setOnClickListener {
            if (!recipient_searchview.isPopupShowing) {
                recipient_searchview.showDropDown()
            }
        }

        RxView.clicks(button_send).bindToLifecycle(button_send).subscribe {
            if (currentRecipientUserId == null) {
                context().toast("Please select a recipient")
                return@subscribe
            }

            val amount = getCurrentAmount()
            if (amount <= 0) {
                context().toast("Please enter a positive amount")
                return@subscribe
            }

            // TODO: send request to server
            val thisUser = userPersistenceManager.getStore()[1]
            val recipientUser = currentRecipientUserId!!

            val description = description_edittext.text.toString()

            //            restClient.transferMoney(thisUser.id, recipientUser.id, amount, description)

            context().toast("Transfered $amount€")
        }

        RxTextView.textChanges(description_edittext).bindToLifecycle(description_edittext)
                .subscribe {
                    currentDescription = it.toString()
                }

        loadUsersFromPersistence()

        updateUserList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        applySavedInstanceState()
    }

    private fun applySavedInstanceState() {
        currentRecipientUserId?.let {
            recipient_avatar.setImageURI(restClient.getUserAvatarURL(it))
        }

        money_amount_edittext.value = savedValue
        description_edittext.setText(currentDescription)
    }

    private fun getCurrentAmount(): Double {
        return money_amount_edittext.value.toDouble() / 100
    }

    private fun setSelectedUser(userEntity: UserEntity) {
        currentRecipientUserId = userEntity.id
        recipient_avatar.setImageURI(restClient.getUserAvatarURL(userEntity.id))
    }

    private fun loadUsersFromPersistence() {
        val persistedUsers = userPersistenceManager.getStore().all
        autocompleteUsersArrayAdapter.setItems(persistedUsers)
    }

    override fun onStop() {
        savedValue = money_amount_edittext.value
        super.onStop()
    }

    private fun updateUserList() {
        restClient.getAllUsers().map { it.map { it.toEntity() } }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .bindUntilEvent(this, Lifecycle.Event.ON_STOP).subscribeBy(onSuccess = {
                    userPersistenceManager.getStore().removeAll()
                    userPersistenceManager.getStore().put(it)
                    loadUsersFromPersistence()
                }, onError = {
                    if (it is CancellationException) {
                        Timber.d { "reload from source cancelled" }
                    }
                })

    }

}
