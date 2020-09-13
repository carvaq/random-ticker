package com.fanstaticapps.randomticker.injection

import android.app.Application
import com.fanstaticapps.randomticker.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object ProvidesModules {
    @Provides
    fun provideUserPreferences(context: Application): UserPreferences {
        return UserPreferences(context)
    }
}