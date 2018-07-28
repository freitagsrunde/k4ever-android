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

        createListeners()

        Fresco.initialize(this)

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
