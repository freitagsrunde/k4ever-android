package de.markusressel.k4ever.application

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
        return DaggerAppComponent
                .builder()
                .create(this)
    }

    override fun onCreate() {
        super
                .onCreate()
        // register app lifecycle
        registerActivityLifecycleCallbacks(AppLifecycleTracker())

        // Clear DB entirely
        //        BoxStore
        //                .deleteAllFiles(applicationContext, null)

        plantTimberTrees()

        createListeners()


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
            Timber
                    .plant(Timber.DebugTree())
        }
    }

}
