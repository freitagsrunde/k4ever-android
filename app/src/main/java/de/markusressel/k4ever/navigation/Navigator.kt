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

package de.markusressel.k4ever.navigation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.util.Colors
import com.mikepenz.materialdrawer.Drawer
import de.markusressel.k4ever.R
import de.markusressel.k4ever.view.fragment.preferences.KutePreferencesHolder
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Markus on 07.01.2018.
 */
@Singleton
class Navigator @Inject constructor(private val kutePreferencesHolder: KutePreferencesHolder

) {

    private val stateStack: Stack<NavigationState> = Stack()

    val currentState: NavigationState
        get() {
            return if (stateStack.isEmpty()) {
                NavigationState(INITIAL_PAGE, NavigationPageHolder.ProductsList)
            } else {
                stateStack.peek()
            }
        }

    lateinit var activity: AppCompatActivity
    lateinit var drawer: Drawer

    /**
     * Navigate to a specific page
     *
     * @param activityContext activity context
     * @param page the page to navigate to
     */
    fun startActivity(activityContext: Context, page: NavigationPage) {
        startActivity(activityContext, page, null)
    }

    /**
     * Navigate to a specific page
     */
    fun navigateTo(drawerMenuItem: DrawerMenuItem, bundle: Bundle? = null): String {
        // page tag HAS to be set
        drawerMenuItem.navigationPage.tag!!

        // try to find previous fragment
        val newFragment = drawerMenuItem.navigationPage.fragment!!()
        newFragment.arguments = bundle

        if (newFragment is DialogFragment) {
            newFragment.show(activity.supportFragmentManager, drawerMenuItem.navigationPage.tag)
        } else {
            // initiate transaction
            var transaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()

            if (stateStack.isNotEmpty()) {
                transaction = transaction.addToBackStack(null)
            }

            transaction.replace(R.id.contentLayout, newFragment, drawerMenuItem.navigationPage.tag)
                    .commit()
            activity.supportFragmentManager.executePendingTransactions()

            activity.setTitle(drawerMenuItem.title)
            drawer.setSelection(drawerMenuItem.identifier, false)

            // remember page stack
            val newState = NavigationState(drawerMenuItem, drawerMenuItem.navigationPage)
            stateStack.push(newState)
        }

        return drawerMenuItem.navigationPage.tag
    }

    /**
     * Navigate to a specific page using the passed in flags
     *
     * @param activityContext activity context
     * @param page the page to navigate to
     * @param flags Intent flags
     */
    fun startActivity(activityContext: Context, page: NavigationPage, flags: Int?) {
        if (page == NavigationPageHolder.About) {
            navigateToAbout(activityContext)
            return
        }

        val intent = page.createIntent(activityContext)
        flags?.let {
            intent.addFlags(it)
        }

        activityContext.startActivity(intent)
    }

    private fun navigateToAbout(activityContext: Context) {
        val themeVal = kutePreferencesHolder.themePreference.persistedValue

        val aboutLibTheme: Libs.ActivityStyle
        aboutLibTheme = if (themeVal == activityContext.getString(R.string.theme_light_value)) {
            Libs.ActivityStyle.LIGHT_DARK_TOOLBAR
        } else {
            Libs.ActivityStyle.DARK
        }

        LibsBuilder().withActivityStyle(aboutLibTheme)
                .withActivityColor(
                        Colors(ContextCompat.getColor(activityContext, R.color.colorPrimary),
                                ContextCompat.getColor(activityContext, R.color.colorPrimaryDark)))
                .withActivityTitle(activityContext.getString(R.string.menu_item_about))
                .start(activityContext)
    }

    /**
     * Navigate back to the previous page
     *
     * @return the page that is navigated back to, null if backstack is empty
     */
    fun navigateBack(): NavigationState? {
        if (stateStack.isEmpty()) {
            return null
        }

        // remove current state
        stateStack.pop()

        // navigate to previous one if there is one
        if (stateStack.isNotEmpty()) {
            val previousState = stateStack.peek()

            activity.supportFragmentManager.popBackStack()

            activity.setTitle(previousState.drawerMenuItem.title)
            drawer.setSelection(previousState.drawerMenuItem.identifier, false)

            return previousState
        }

        return null
    }

    fun initDrawer() {
        navigateTo(currentState.drawerMenuItem)
    }

    companion object {
        val DrawerItems = DrawerItemHolder
        val NavigationPages = NavigationPageHolder

        val INITIAL_PAGE = DrawerItemHolder.ProductList
    }

}