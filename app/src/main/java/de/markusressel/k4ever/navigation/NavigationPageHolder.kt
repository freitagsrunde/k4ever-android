package de.markusressel.k4ever.navigation

import de.markusressel.k4ever.view.activity.MainActivity
import de.markusressel.k4ever.view.fragment.account.AccountTabFragment
import de.markusressel.k4ever.view.fragment.moneytransfer.MoneyTransferFragment
import de.markusressel.k4ever.view.fragment.preferences.PreferencesFragment
import de.markusressel.k4ever.view.fragment.products.ProductsFragment

/**
 * Created by Markus on 08.01.2018.
 */
object NavigationPageHolder {

    val Main: NavigationPage = NavigationPage(activityClass = MainActivity::class.java)

    val ProductsList: NavigationPage = NavigationPage(fragment = ::ProductsFragment, tag = "ProductsFragment")

    val Account: NavigationPage = NavigationPage(fragment = ::AccountTabFragment, tag = "AccountTabFragment")
    val MoneyTransfer: NavigationPage = NavigationPage(fragment = ::MoneyTransferFragment, tag = "MoneyTransferFragment")

    val Settings = NavigationPage(fragment = ::PreferencesFragment, tag = "PreferencesFragment")
    //    val Settings = NavigationPage(activityClass = PreferenceOverviewActivity::class.java)

    val About = NavigationPage()

}