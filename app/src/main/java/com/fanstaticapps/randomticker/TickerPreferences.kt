package com.fanstaticapps.randomticker

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import androidx.preference.PreferenceManager

class TickerPreferences(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val currentSelectedIdPref: String = "currentSelectedId"

        /**
         * These preferences are changed by the user in the @see SettingsActivity.
         * The preference keys (@see constants.xml) are also present in the xml, therefor the same should be used.
         */
        private const val ringtonePref: String = "pref_ringtone"
        private const val vibratorPref: String = "pref_vibration"
    }

    var alarmRingtone: String
        get() {
            val defaultFallback = "content://settings/system/notification_sound"
            return prefs.getString(
                ringtonePref,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString()
            )
                ?: defaultFallback
        }
        set(value) = prefs.edit().putString(ringtonePref, value).apply()
    var currentSelectedId: Long
        get() = prefs.getLong(currentSelectedIdPref, 0)
        set(value) = prefs.edit().putLong(currentSelectedIdPref, value).apply()
    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(vibratorPref, false)
        set(value) = prefs.edit().putBoolean(vibratorPref, value).apply()

}