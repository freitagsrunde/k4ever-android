package de.markusressel.k4ever.dagger

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import de.markusressel.k4ever.application.App
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (AndroidSupportInjectionModule::class)])
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()

}
