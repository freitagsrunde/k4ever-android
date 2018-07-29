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
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.github.ajalt.timberkt.Timber
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import de.markusressel.k4ever.R
import de.markusressel.k4ever.dagger.module.Implementation
import de.markusressel.k4ever.dagger.module.ImplementationTypeEnum
import de.markusressel.k4ever.data.persistence.user.UserEntity
import de.markusressel.k4ever.data.persistence.user.UserPersistenceManager
import de.markusressel.k4ever.extensions.common.android.context
import de.markusressel.k4ever.extensions.data.toEntity
import de.markusressel.k4ever.rest.K4EverRestApiClient
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import de.markusressel.k4ever.view.fragment.base.DaggerSupportFragmentBase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment__money_transfer.*
import java.text.DecimalFormat
import java.util.concurrent.CancellationException
import javax.inject.Inject


/**
 * Server Status fragment
 *
 * Created by Markus on 07.01.2018.
 */
class MoneyTransferFragment : DaggerSupportFragmentBase() {

    override val layoutRes: Int
        get() = R.layout.fragment__money_transfer

    @Inject
    lateinit var userPersistenceManager: UserPersistenceManager

    @Inject
    @field:Implementation(ImplementationTypeEnum.DUMMY)
    lateinit var restClient: K4EverRestApiClient

    private val optionsMenuComponent: OptionsMenuComponent by lazy {
        OptionsMenuComponent(this, optionsMenuRes = R.menu.options_menu_none)
    }

    override fun initComponents(context: Context) {
        super.initComponents(context)
        optionsMenuComponent
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        optionsMenuComponent.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (super.onOptionsItemSelected(item)) {
            return true
        }
        return optionsMenuComponent.onOptionsItemSelected(item)
    }

    private lateinit var autocompleteUsersArrayAdapter: UserArrayAdapter
    private var currentUser: UserEntity? = null

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

        money_amount_edittext.setText("0,00")
        money_amount_edittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO: doesn't work as expected

                val cursorIndexBeforeChange = money_amount_edittext.selectionStart

                val df = DecimalFormat("####0.00")
                val sourceAsDouble = s.toString().replace(",", ".").toDouble()
                val result = df.format(sourceAsDouble).replace(".", ",")

                money_amount_edittext.removeTextChangedListener(this)
                money_amount_edittext.setText(result)
                money_amount_edittext.setSelection(
                        cursorIndexBeforeChange.coerceIn(0, result.length))
                money_amount_edittext.addTextChangedListener(this)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        loadUsersFromPersistence()

        updateUserList()
    }

    private fun setSelectedUser(userEntity: UserEntity) {
        currentUser = userEntity
        recipient_avatar.setImageURI(restClient.getUserAvatarURL(userEntity.id))
    }

    private fun loadUsersFromPersistence() {
        val persistedUsers = userPersistenceManager.getStore().all
        autocompleteUsersArrayAdapter.setItems(persistedUsers)
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
