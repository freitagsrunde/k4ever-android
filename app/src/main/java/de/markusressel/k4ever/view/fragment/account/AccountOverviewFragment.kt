package de.markusressel.k4ever.view.fragment.account

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import de.markusressel.k4ever.R
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import de.markusressel.k4ever.view.fragment.base.DaggerSupportFragmentBase


/**
 * Server Status fragment
 *
 * Created by Markus on 07.01.2018.
 */
class AccountOverviewFragment : DaggerSupportFragmentBase() {

    override val layoutRes: Int
        get() = R.layout.fragment__account__overview

    private val optionsMenuComponent: OptionsMenuComponent by lazy {
        OptionsMenuComponent(this, optionsMenuRes = R.menu.options_menu_none, onCreateOptionsMenu = { menu: Menu?, menuInflater: MenuInflater? ->
        }, onOptionsMenuItemClicked = {
            false
        })
    }

    override fun initComponents(context: Context) {
        super
                .initComponents(context)
        optionsMenuComponent
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super
                .onCreateOptionsMenu(menu, inflater)
        optionsMenuComponent
                .onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (super.onOptionsItemSelected(item)) {
            return true
        }
        return optionsMenuComponent
                .onOptionsItemSelected(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super
                .onViewCreated(view, savedInstanceState)
    }

}
