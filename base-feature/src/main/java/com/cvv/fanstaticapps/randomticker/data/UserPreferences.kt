package com.cvv.fanstaticapps.randomticker.data

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.preference.PreferenceManager

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context)

    private val generatedIntervalPref: String = "generatedInterval"
    private val generatedIntervalEndTimePref: String = "generatedIntervalEndTime"
    private val tickerRunningPref: String = "tickerRunning"
    /**
     * These preferences are changed by the user in the @see SettingsActivity.
     * The preference keys (@see constants.xml) are also present in the xml, therefor the same should be used.
     */
    private val showNotificationPref: String = "pref_show_notification"
    private val ringtonePref: String = "pref_ringtone"

    var interval: Long
        get() = prefs.getLong(generatedIntervalPref, 0)
        set(value) = prefs.edit().putLong(generatedIntervalPref, value).apply()
    var intervalFinished: Long
        get() = prefs.getLong(generatedIntervalEndTimePref, 0)
        set(value) = prefs.edit().putLong(generatedIntervalEndTimePref, value).apply()
    var currentlyTickerRunning: Boolean
        get() = prefs.getBoolean(tickerRunningPref, false)
        set(value) = prefs.edit().putBoolean(tickerRunningPref, value).apply()
    var showNotification: Boolean
        get() = prefs.getBoolean(showNotificationPref, false)
        set(value) = prefs.edit().putBoolean(showNotificationPref, value).apply()
    var alarmRingtone: String
        get() = prefs.getString(ringtonePref, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString())
        set(value) = prefs.edit().putString(ringtonePref, value).apply()
}