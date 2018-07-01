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
import de.markusressel.k4ever.view.fragment.ProductsListFragment
import de.markusressel.k4ever.view.fragment.preferences.PreferencesFragment
import de.markusressel.kutepreferences.library.persistence.DefaultKutePreferenceDataProvider
import de.markusressel.kutepreferences.library.persistence.KutePreferenceDataProvider
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
    internal abstract fun ProductsListFragment(): ProductsListFragment

    @ContributesAndroidInjector
    internal abstract fun PreferencesFragment(): PreferencesFragment

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
        internal fun provideKutePreferenceDataProvider(context: Context): KutePreferenceDataProvider {
            return DefaultKutePreferenceDataProvider(context)
        }

        //        @Provides
        //        @Singleton
        //        @JvmStatic
        //        internal fun provideBoxStore(context: Context): BoxStore {
        //            return MyObjectBox
        //                    .builder()
        //                    .androidContext(context)
        //                    .build()
        //        }

    }

}