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

package de.markusressel.k4ever.application

import co.lokalise.android.sdk.LokaliseSDK
import com.facebook.drawee.backends.pipeline.Fresco
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import de.markusressel.k4ever.BuildConfig
import de.markusressel.k4ever.dagger.DaggerAppComponent
import timber.log.Timber

/**
 * Created by Markus on 30.06.2018.
 */
class App : DaggerApplicationBase() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        // register app lifecycle
        registerActivityLifecycleCallbacks(AppLifecycleTracker())

        // Clear DB entirely
        //        BoxStore
        //                .deleteAllFiles(applicationContext, null)

        plantTimberTrees()

        initLokalise()

        createListeners()

        Fresco.initialize(this)

    }

    private fun initLokalise() {
        // Initialise Lokalise SDK with projects SDK token and project id
        // It is important to call this right after the "super.onCreate()"
        // If you are using AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        // make sure it is called before LokaliseSDK.init()
        LokaliseSDK.init("f1def8862d1499e05f7aa6f6e5b0482bef9d99ca", "481343335d3e1e02b4b8f6.52342493", this)

        // Add this only if you want to use pre-release bundles
        LokaliseSDK.setPreRelease(true)

        // Fetch the latest translations from Lokalise (can be called anywhere)
        LokaliseSDK.updateTranslations()
    }

    private fun createListeners() {
        //        Bus
        //                .observe<HostChangedEvent>()
        //                .subscribe {
        //                    restClient
        //                            .setHostname(it.host)
        //                }
        //                .registerInBus(this)

    }

    private fun plantTimberTrees() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}
