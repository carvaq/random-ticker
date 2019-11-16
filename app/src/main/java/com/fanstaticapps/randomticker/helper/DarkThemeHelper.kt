package com.fanstaticapps.randomticker.helper

import androidx.appcompat.app.AppCompatDelegate
import com.fanstaticapps.randomticker.UserPreferences

fun setDarkTheme(prefs: UserPreferences) {
    if (prefs.darkTheme) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}