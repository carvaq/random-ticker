package com.cvv.fanstaticapps.randomticker

import com.cvv.fanstaticapps.randomticker.data.UserPreferences
import com.cvv.fanstaticapps.randomticker.injection.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

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

        super.onCreate()
    }
}
