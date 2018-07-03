package de.markusressel.k4ever.dagger

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import de.markusressel.k4ever.application.App
import de.markusressel.k4ever.dagger.module.PersistenceBindingsModule
import de.markusressel.k4ever.dagger.module.RestBindingsModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (PersistenceBindingsModule::class), (RestBindingsModule::class), (AndroidSupportInjectionModule::class)])
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()

}
