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

package de.markusressel.k4ever.view.activity.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.ajalt.timberkt.Timber
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import de.markusressel.k4ever.R
import de.markusressel.k4ever.extensions.common.android.isTablet
import de.markusressel.k4ever.navigation.DrawerItemHolder
import de.markusressel.k4ever.navigation.DrawerItemHolder.About
import de.markusressel.k4ever.navigation.DrawerItemHolder.Account
import de.markusressel.k4ever.navigation.DrawerItemHolder.MoneyTransfer
import de.markusressel.k4ever.navigation.DrawerItemHolder.ProductList
import de.markusressel.k4ever.navigation.DrawerItemHolder.Settings
import de.markusressel.k4ever.navigation.DrawerMenuItem
import de.markusressel.k4ever.navigation.Navigator
import de.markusressel.k4ever.view.fragment.preferences.PreferencesFragment
import kotlinx.android.synthetic.main.activity__main.*
import kotlinx.android.synthetic.main.view__toolbar.*
import java.util.*

/**
 * A base activity that provides the full navigation navigationDrawer implementation
 *
 * Created by Markus on 07.01.2018.
 */
abstract class NavigationDrawerActivity : DaggerSupportActivityBase() {

    override val layoutRes: Int
        get() = R.layout.activity__main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigator.activity = this

        val menuItemList = initDrawerMenuItems()
        val accountHeader = initAccountHeader()

        val builder = DrawerBuilder().withActivity(this).withAccountHeader(accountHeader).withDrawerItems(menuItemList)
                .withCloseOnClick(false).withToolbar(toolbar).withSavedInstance(savedInstanceState)

        val navigationDrawer: Drawer
        if (isTablet()) {
            navigationDrawer = builder.buildView()

            drawerLayoutParent.visibility = View.VISIBLE
            drawerDividerView.visibility = View.VISIBLE

            drawerLayoutParent.addView(navigationDrawer.slider, 0)
        } else {
            drawerLayoutParent.visibility = View.GONE
            drawerDividerView.visibility = View.GONE

            navigationDrawer = builder.build()
        }

        navigator.drawer = navigationDrawer

        if (savedInstanceState == null) {
            navigator.initDrawer()
        }
    }

    override fun onStart() {
        super.onStart()

        //        Bus
        //                .observe<ThemeChangedEvent>()
        //                .subscribe {
        //                    restartActivity()
        //                }
        //                .registerInBus(this)
    }

    private fun restartActivity() {
        navigator.startActivity(this, Navigator.NavigationPages.Main, Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        navigator.navigateTo(DrawerItemHolder.Settings)
    }

    private fun initAccountHeader(): AccountHeader {
        val profiles: MutableList<IProfile<*>> = getProfiles()

        return AccountHeaderBuilder().withActivity(this)
                //                .withProfiles(profiles)
                //                .withCloseDrawerOnProfileListClick(false)
                //                .withCurrentProfileHiddenInList(true)
                //                .withHeaderBackground()
                .withDividerBelowHeader(true).withOnAccountHeaderListener(object : AccountHeader.OnAccountHeaderListener {
                    override fun onProfileChanged(view: View?, profile: IProfile<*>, current: Boolean): Boolean {
                        Timber.d { "Pressed profile: '$profile' with current: '$current'" }
                        return false
                    }
                })

                .build()
    }

    private fun getProfiles(): MutableList<IProfile<*>> {
        val profiles: MutableList<IProfile<*>> = LinkedList()

        // TODO: implement different servers as profiles

        profiles.add(ProfileDrawerItem().withName("Markus Ressel").withEmail("mail@markusressel.de").withIcon(R.mipmap.ic_launcher))

        return profiles
    }

    private fun initDrawerMenuItems(): MutableList<IDrawerItem<*>> {
        val menuItemList: MutableList<IDrawerItem<*>> = LinkedList()

        val clickListener = object : Drawer.OnDrawerItemClickListener {

            override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                if (drawerItem.identifier == navigator.currentState.drawerMenuItem.identifier) {
                    Timber.d { "Closing navigationDrawer because the clicked item (${drawerItem.identifier}) is the currently active page" }
                    if (!isTablet()) {

                        navigator.drawer.closeDrawer()
                    }
                    return true
                }

                val drawerMenuItem = DrawerItemHolder.fromId(drawerItem.identifier)

                drawerMenuItem?.navigationPage?.let {
                    if (it.fragment != null) {
                        navigator.navigateTo(drawerMenuItem)
                    } else {
                        navigator.startActivity(applicationContext, it)
                    }

                    if (drawerItem.isSelectable) {
                        // set new title
                        setTitle(drawerMenuItem.title)
                    }

                    if (!isTablet()) {
                        navigator.drawer.closeDrawer()
                    }
                    return true
                }

                return false
            }
        }


        listOf(ProductList, Account, MoneyTransfer).forEach {
            menuItemList.add(createPrimaryMenuItem(it, clickListener))
        }

        menuItemList.add(DividerDrawerItem())

        menuItemList.add(createPrimaryMenuItem(Settings, clickListener))

        menuItemList.add(createSecondaryMenuItem(About, clickListener))

        return menuItemList
    }

    private fun createPrimaryMenuItem(menuItem: DrawerMenuItem, clickListener: Drawer.OnDrawerItemClickListener): PrimaryDrawerItem {
        return PrimaryDrawerItem().withName(menuItem.title).withIdentifier(menuItem.identifier).withIcon(menuItem.getIcon(iconHandler))
                .withSelectable(menuItem.selectable).withOnDrawerItemClickListener(clickListener)
    }

    private fun createSecondaryMenuItem(menuItem: DrawerMenuItem, clickListener: Drawer.OnDrawerItemClickListener): SecondaryDrawerItem {
        return SecondaryDrawerItem().withName(menuItem.title).withIdentifier(menuItem.identifier).withIcon(menuItem.getIcon(iconHandler))
                .withSelectable(menuItem.selectable).withOnDrawerItemClickListener(clickListener)
    }

    override fun onBackPressed() {
        if (navigator.drawer.isDrawerOpen) {
            navigator.drawer.closeDrawer()
            return
        }

        // special case for preferences
        val preferenceFragment: Fragment? = supportFragmentManager.findFragmentByTag(navigator.currentState.drawerMenuItem.navigationPage.tag)
        if (preferenceFragment is PreferencesFragment && preferenceFragment.isVisible) {
            if (preferenceFragment.onBackPressed()) {
                return
            }
        }

        val previousPage = navigator.navigateBack()
        if (previousPage != null) {
            navigator.drawer.setSelection(previousPage.drawerMenuItem.identifier, false)
            return
        }

        super.onBackPressed()
    }
}