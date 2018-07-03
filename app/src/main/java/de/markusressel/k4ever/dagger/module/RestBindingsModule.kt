package de.markusressel.k4ever.dagger.module

import dagger.Module
import dagger.Provides
import de.markusressel.k4ever.rest.K4EverRestClient
import javax.inject.Singleton

/**
 * Created by Markus on 03.7.2018.
 */
@Module
abstract class RestBindingsModule {

    @Module
    companion object {

        @Provides
        @Singleton
        @JvmStatic
        internal fun provideRestClient(): K4EverRestClient {
            return K4EverRestClient()
        }

    }

}