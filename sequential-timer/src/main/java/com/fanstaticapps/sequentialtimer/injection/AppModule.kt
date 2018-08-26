package com.fanstaticapps.randomticker.injection

import android.content.Context
import com.fanstaticapps.randomticker.TickerApplication
import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {

    @Binds
    abstract fun provideContext(application: TickerApplication): Context


}
