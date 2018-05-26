package com.cvv.fanstaticapps.randomticker.injection

import com.cvv.fanstaticapps.randomticker.TickerApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton


@Singleton
@Component(modules = [ActivityBuilderModule::class, AndroidInjectionModule::class])
interface AppComponent : AndroidInjector<TickerApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: TickerApplication): Builder

        fun build(): AppComponent
    }

}