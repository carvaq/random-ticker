package com.fanstaticapps.randomticker

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import androidx.preference.PreferenceManager
import com.fanstaticapps.randomticker.helper.livedata.SharedPreferenceLongLiveData

class TickerPreferences(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val generatedIntervalPref: String = "generatedInterval"
        private const val generatedIntervalEndTimePref: String = "generatedIntervalEndTime"
        private const val currentSelectedIdPref: String = "currentSelectedId"

        /**
         * These preferences are changed by the user in the @see SettingsActivity.
         * The preference keys (@see constants.xml) are also present in the xml, therefor the same should be used.
         */
        private const val showNotificationPref: String = "pref_show_notification"
        private const val ringtonePref: String = "pref_ringtone"
        private const val vibratorPref: String = "pref_vibration"
        private const val darkThemePref: String = "pref_dark_theme"
    }

    var interval: Long
        get() = prefs.getLong(generatedIntervalPref, -1)
        private set(value) = prefs.edit().putLong(generatedIntervalPref, value).apply()
    var intervalWillBeFinished: Long
        get() = prefs.getLong(generatedIntervalEndTimePref, -1)
        private set(value) = prefs.edit().putLong(generatedIntervalEndTimePref, value).apply()
    var showRunningTimerNotification: Boolean
        get() = prefs.getBoolean(showNotificationPref, false)
        set(value) = prefs.edit().putBoolean(showNotificationPref, value).apply()
    var alarmRingtone: String
        get() {
            val defaultFallback = "content://settings/system/notification_sound"
            return prefs.getString(ringtonePref, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString())
                    ?: defaultFallback
        }
        set(value) = prefs.edit().putString(ringtonePref, value).apply()
    var currentSelectedId: Long
        get() = prefs.getLong(currentSelectedIdPref, 0)
        set(value) = prefs.edit().putLong(currentSelectedIdPref, value).apply()
    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(vibratorPref, false)
        set(value) = prefs.edit().putBoolean(vibratorPref, value).apply()
    var darkTheme: Boolean
        get() = prefs.getBoolean(darkThemePref, false)
        set(value) = prefs.edit().putBoolean(darkThemePref, value).apply()

    val currentSelectedBookmarkIdAsLiveData = SharedPreferenceLongLiveData(prefs, currentSelectedIdPref, 0)

    fun setTickerInterval(interval: Long) {
        this.interval = interval
        this.intervalWillBeFinished = System.currentTimeMillis() + interval
    }

    fun resetInterval() {
        this.interval = -1
        this.intervalWillBeFinished = -1
    }
}
