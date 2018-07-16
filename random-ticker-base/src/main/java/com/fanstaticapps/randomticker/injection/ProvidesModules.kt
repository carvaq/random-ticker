package com.fanstaticapps.randomticker.injection

import android.content.Context
import com.fanstaticapps.common.UserPreferences
import dagger.Module
import dagger.Provides

@Module
class ProvidesModule {

    @Provides
    fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences(context)
    }
}