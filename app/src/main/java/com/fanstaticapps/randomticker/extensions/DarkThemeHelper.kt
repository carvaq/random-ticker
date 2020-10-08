package com.fanstaticapps.randomticker.extensions

import androidx.appcompat.app.AppCompatDelegate
import com.fanstaticapps.randomticker.TickerPreferences

fun setDarkTheme(prefs: TickerPreferences) {
    if (prefs.darkTheme) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}