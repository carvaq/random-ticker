package com.fanstaticapps.randomticker.injection

import android.app.Application
import com.fanstaticapps.randomticker.TickerPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
object ProvidesModules {
    @Provides
    fun provideUserPreferences(context: Application): TickerPreferences {
        return TickerPreferences(context)
    }

    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}