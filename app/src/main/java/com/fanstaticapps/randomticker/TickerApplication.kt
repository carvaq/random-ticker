package com.fanstaticapps.randomticker

import android.app.Application
import com.fanstaticapps.randomticker.extensions.setDarkTheme
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


/**
 * Created by carvaq
 * Date: 20/09/2017
 * Project: RandomTicker
 */


@HiltAndroidApp
class TickerApplication : Application() {

    @Inject
    lateinit var preferences: UserPreferences

    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        setDarkTheme(preferences)

        super.onCreate()
    }
}
