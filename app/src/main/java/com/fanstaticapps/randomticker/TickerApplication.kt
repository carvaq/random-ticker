package com.fanstaticapps.randomticker

import com.fanstaticapps.randomticker.extensions.setDarkTheme
import com.fanstaticapps.randomticker.injection.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * Created by carvaq
 * Date: 20/09/2017
 * Project: RandomTicker
 */

val PREFS: UserPreferences by lazy {
    TickerApplication.prefs!!
}

class TickerApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return androidInjector
    }

    companion object {
        var prefs: UserPreferences? = null
    }

    lateinit var androidInjector: AndroidInjector<out DaggerApplication>


    override fun onCreate() {
        prefs = UserPreferences(this)
        androidInjector = DaggerAppComponent
                .builder()
                .application(this)
                .build()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        setDarkTheme(PREFS)
        super.onCreate()
    }
}
