package com.fanstaticapps.randomticker.preferences

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import androidx.datastore.DataMigration
import androidx.preference.PreferenceManager

class UserPreferencesMigration(context: Context) : DataMigration<UserPreferences> {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override suspend fun cleanUp() {

    }

    override suspend fun migrate(currentData: UserPreferences): UserPreferences {
        val defaultFallback = "content://settings/system/notification_sound"
        val showRunningTimerNotification = prefs.getBoolean("pref_show_notification", false)
        val alarmRingtone = prefs.getString("pref_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString())
        val vibrationEnabled = prefs.getBoolean("pref_vibration", false)
        val darkTheme = prefs.getBoolean("pref_dark_theme", false)
        return UserPreferences.newBuilder(currentData)
                .setMigrationCompleted(true)
                .setDarkTheme(darkTheme)
                .setRingtone(alarmRingtone ?: defaultFallback)
                .setShowNotification(showRunningTimerNotification)
                .setVibrationEnabled(vibrationEnabled)
                .build()
    }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean {
        return currentData.migrationCompleted
    }

}