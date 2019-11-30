package com.fanstaticapps.randomticker.injection

import com.fanstaticapps.randomticker.TickerApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton


@Singleton
@Component(modules = [
    ComponentsModule::class,
    AndroidInjectionModule::class,
    ProvidesModule::class,
    AppModule::class])
interface AppComponent : AndroidInjector<TickerApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: TickerApplication): Builder

        fun build(): AppComponent
    }

}