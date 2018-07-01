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

    val Settings = DrawerMenuItem(title = R.string.menu_item_settings, icon = MaterialDesignIconic.Icon.gmi_settings, selectable = false, navigationPage = NavigationPageHolder.Settings)

    val About = DrawerMenuItem(title = R.string.menu_item_about, icon = MaterialDesignIconic.Icon.gmi_info, selectable = false, navigationPage = NavigationPageHolder.About)

    fun fromId(drawerItemIdentifier: Long): DrawerMenuItem? {
        return when (drawerItemIdentifier) {
            ProductList.identifier -> ProductList
            Account.identifier -> Account
            Settings.identifier -> Settings
            About.identifier -> About
            else -> {
                Timber
                        .w { "Unknown menu item identifier: $drawerItemIdentifier" }
                null
            }
        }
    }

}