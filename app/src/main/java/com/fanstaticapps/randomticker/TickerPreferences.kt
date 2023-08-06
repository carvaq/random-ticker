package com.fanstaticapps.randomticker

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class TickerPreferences(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val currentSelectedIdPref: String = "currentSelectedId"
    }

    var currentSelectedId: Long
        get() = prefs.getLong(currentSelectedIdPref, 0)
        set(value) = prefs.edit().putLong(currentSelectedIdPref, value).apply()
}