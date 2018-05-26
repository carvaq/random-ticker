package com.cvv.fanstaticapps.randomticker.helper

import android.content.Context
import android.content.SharedPreferences

class TickerData(context: Context) {
    private val PREFS_FILENAME = "com.cvv.fanstaticapps.randomticker_preferences"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    private val MININUM_MINUTES: String = "minimumMinutes"
    private val MININUM_SECONS: String = "minimumSeconds"
    private val MAXIMUM_MINUTES: String = "maximumMinutes"
    private val MAXIMUM_SECONDS: String = "maximumSeconds"
    private val GENERATED_INTERVAL: String = "generatedInterval"
    private val GENERATED_INTERVAL_END_TIME: String = "generatedIntervalEndTime"
    private val IS_TICKER_CURRENTLY_RUNNING: String = "tickerRunning"

    var minMin: Int
        get() = prefs.getInt(MININUM_MINUTES, 0)
        set(value) = prefs.edit().putInt(MININUM_MINUTES, value).apply()
    var minSec: Int
        get() = prefs.getInt(MININUM_SECONS, 0)
        set(value) = prefs.edit().putInt(MININUM_SECONS, value).apply()
    var maxMin
        get() = prefs.getInt(MAXIMUM_MINUTES, 5)
        set(value) = prefs.edit().putInt(MAXIMUM_MINUTES, value).apply()
    var maxSec: Int
        get() = prefs.getInt(MAXIMUM_SECONDS, 0)
        set(value) = prefs.edit().putInt(MAXIMUM_SECONDS, value).apply()
    var interval: Long
        get() = prefs.getLong(GENERATED_INTERVAL, 0)
        set(value) = prefs.edit().putLong(GENERATED_INTERVAL, value).apply()
    var intervalFinished: Long
        get() = prefs.getLong(GENERATED_INTERVAL_END_TIME, 0)
        set(value) = prefs.edit().putLong(GENERATED_INTERVAL_END_TIME, value).apply()
    var currentlyTickerRunning: Boolean
        get() = prefs.getBoolean(IS_TICKER_CURRENTLY_RUNNING, false)
        set(value) = prefs.edit().putBoolean(IS_TICKER_CURRENTLY_RUNNING, value).apply()
}