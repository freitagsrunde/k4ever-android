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

import com.github.ajalt.timberkt.Timber
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic
import de.markusressel.k4ever.R

/**
 * Created by Markus on 08.01.2018.
 */
object DrawerItemHolder {

    val ProductList = DrawerMenuItem(title = R.string.menu_item_products, icon = MaterialDesignIconic.Icon.gmi_shopping_basket, selectable = true, navigationPage = NavigationPageHolder.ProductsList)
    val Account = DrawerMenuItem(title = R.string.menu_item_account, icon = MaterialDesignIconic.Icon.gmi_account, selectable = true, navigationPage = NavigationPageHolder.Account)
    val MoneyTransfer = DrawerMenuItem(title = R.string.menu_item__money_transfer, icon = MaterialDesignIconic.Icon.gmi_money_off, selectable = true, navigationPage = NavigationPageHolder.MoneyTransfer)

    val Settings = DrawerMenuItem(title = R.string.menu_item_settings, icon = MaterialDesignIconic.Icon.gmi_settings, selectable = true, navigationPage = NavigationPageHolder.Settings)

    val About = DrawerMenuItem(title = R.string.menu_item_about, icon = MaterialDesignIconic.Icon.gmi_info, selectable = false, navigationPage = NavigationPageHolder.About)

    fun fromId(drawerItemIdentifier: Long): DrawerMenuItem? {
        return when (drawerItemIdentifier) {
            ProductList.identifier -> ProductList
            Account.identifier -> Account
            MoneyTransfer.identifier -> MoneyTransfer
            Settings.identifier -> Settings
            About.identifier -> About
            else -> {
                Timber.w { "Unknown menu item identifier: $drawerItemIdentifier" }
                null
            }
        }
    }

}