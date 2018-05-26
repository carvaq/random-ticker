package com.cvv.fanstaticapps.randomticker

import com.cvv.fanstaticapps.randomticker.helper.TickerData
import com.cvv.fanstaticapps.randomticker.injection.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

/**
 * Created by Carla
 * Date: 20/09/2017
 * Project: RandomTicker
 */

val prefs: TickerData by lazy {
    TickerApplication.prefs!!
}

class TickerApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return androidInjector
    }

    companion object {
        var prefs: TickerData? = null
    }

    lateinit var androidInjector: AndroidInjector<out DaggerApplication>


    override fun onCreate() {
        prefs = TickerData(this)
        androidInjector = DaggerAppComponent
                .builder()
                .application(this)
                .build()

        super.onCreate()
    }
}
