package com.fanstaticapps.randomticker.injection

import android.app.Application
import com.fanstaticapps.randomticker.TickerPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object ProvidesModules {
    @Provides
    fun provideUserPreferences(context: Application): TickerPreferences {
        return TickerPreferences(context)
    }
}