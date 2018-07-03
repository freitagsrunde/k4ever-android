package de.markusressel.k4ever.navigation

import de.markusressel.k4ever.view.activity.MainActivity
import de.markusressel.k4ever.view.fragment.AccountFragment
import de.markusressel.k4ever.view.fragment.MoneyTransferFragment
import de.markusressel.k4ever.view.fragment.preferences.PreferencesFragment
import de.markusressel.k4ever.view.fragment.products.ProductsListFragment

/**
 * Created by Markus on 08.01.2018.
 */
object NavigationPageHolder {

    val Main: NavigationPage = NavigationPage(activityClass = MainActivity::class.java)

    val ProductsList: NavigationPage = NavigationPage(fragment = ::ProductsListFragment, tag = "ProductsFragment")

    val Account: NavigationPage = NavigationPage(fragment = ::AccountFragment, tag = "AccountFragment")
    val MoneyTransfer: NavigationPage = NavigationPage(fragment = ::MoneyTransferFragment, tag = "MoneyTransferFragment")

    val Settings = NavigationPage(fragment = ::PreferencesFragment, tag = "PreferencesFragment")
    //    val Settings = NavigationPage(activityClass = PreferenceOverviewActivity::class.java)

    val About = NavigationPage()

}