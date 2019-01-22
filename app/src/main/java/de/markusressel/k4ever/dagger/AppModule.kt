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

package de.markusressel.k4ever.dagger

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import de.markusressel.k4ever.application.App
import de.markusressel.k4ever.view.activity.MainActivity
import de.markusressel.k4ever.view.activity.base.DaggerSupportActivityBase
import de.markusressel.k4ever.view.fragment.account.AccountOverviewFragment
import de.markusressel.k4ever.view.fragment.account.AccountTabFragment
import de.markusressel.k4ever.view.fragment.account.BalanceHistoryFragment
import de.markusressel.k4ever.view.fragment.account.transfer.TransferDetailActivity
import de.markusressel.k4ever.view.fragment.account.transfer.TransferDetailContentFragment
import de.markusressel.k4ever.view.fragment.base.DaggerDialogFragmentBase
import de.markusressel.k4ever.view.fragment.base.TabNavigationFragment
import de.markusressel.k4ever.view.fragment.moneytransfer.MoneyTransferFragment
import de.markusressel.k4ever.view.fragment.preferences.PreferencesFragment
import de.markusressel.k4ever.view.fragment.products.ProductDetailActivity
import de.markusressel.k4ever.view.fragment.products.ProductDetailContentFragment
import de.markusressel.k4ever.view.fragment.products.ProductsFragment
import de.markusressel.kutepreferences.core.persistence.DefaultKutePreferenceDataProvider
import de.markusressel.kutepreferences.core.persistence.KutePreferenceDataProvider
import javax.inject.Singleton

/**
 * Created by Markus on 20.12.2017.
 */
@Module
abstract class AppModule {

    @Binds
    internal abstract fun application(application: App): Application

    @ContributesAndroidInjector
    internal abstract fun DaggerSupportActivityBase(): DaggerSupportActivityBase

    @ContributesAndroidInjector
    internal abstract fun MainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun ProductsListFragment(): ProductsFragment

    @ContributesAndroidInjector
    internal abstract fun TabNavigationFragment(): TabNavigationFragment

    @ContributesAndroidInjector
    internal abstract fun AccountTabFragment(): AccountTabFragment

    @ContributesAndroidInjector
    internal abstract fun AccountFragment(): AccountOverviewFragment

    @ContributesAndroidInjector
    internal abstract fun BalanceHistoryFragment(): BalanceHistoryFragment

    @ContributesAndroidInjector
    internal abstract fun TransactionFragment(): MoneyTransferFragment

    @ContributesAndroidInjector
    internal abstract fun PreferencesFragment(): PreferencesFragment

    @ContributesAndroidInjector
    internal abstract fun ProductDetailActivity(): ProductDetailActivity

    @ContributesAndroidInjector
    internal abstract fun ProductDetailContentFragment(): ProductDetailContentFragment

    @ContributesAndroidInjector
    internal abstract fun TransferDetailActivity(): TransferDetailActivity

    @ContributesAndroidInjector
    internal abstract fun TransferDetailContentFragment(): TransferDetailContentFragment

    @ContributesAndroidInjector
    internal abstract fun DaggerDialogFragmentBase(): DaggerDialogFragmentBase



    @Module
    companion object {

        @Provides
        @Singleton
        @JvmStatic
        internal fun provideContext(application: Application): Context {
            return application
        }

        @Provides
        @Singleton
        @JvmStatic
        internal fun provideKutePreferenceDataProvider(
                context: Context): KutePreferenceDataProvider {
            return DefaultKutePreferenceDataProvider(context)
        }

    }

}
