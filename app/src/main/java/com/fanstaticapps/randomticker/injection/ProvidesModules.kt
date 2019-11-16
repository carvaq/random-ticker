package com.fanstaticapps.randomticker.injection

import android.content.Context
import com.fanstaticapps.randomticker.UserPreferences
import dagger.Module
import dagger.Provides

@Module
class ProvidesModule {

    @Provides
    fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences(context)
    }
}