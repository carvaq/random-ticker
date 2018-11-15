package com.fanstaticapps.common.helper

import androidx.appcompat.app.AppCompatDelegate
import com.fanstaticapps.common.UserPreferences

fun setDarkTheme(prefs: UserPreferences) {
    if (prefs.darkTheme) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}