package com.fanstaticapps.randomticker.injection

import android.app.Application
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import com.fanstaticapps.randomticker.preferences.TickerPreferences
import com.fanstaticapps.randomticker.preferences.UserPreferences
import com.fanstaticapps.randomticker.preferences.UserPreferencesMigration
import com.fanstaticapps.randomticker.preferences.UserPreferencesSerializer
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

    @Provides
    fun providesUserPreferencesDataStore(context: Application): DataStore<UserPreferences> {
        return context.createDataStore(
                fileName = "app_prefs.pb",
                serializer = UserPreferencesSerializer,
                migrations = listOf(UserPreferencesMigration(context)))
    }

}