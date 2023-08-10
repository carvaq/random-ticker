package com.fanstaticapps.randomticker.helper

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.createNotificationChannel
import com.fanstaticapps.randomticker.extensions.getNotificationManager

class MigrationService(
    private val context: Context,
    private val bookmarkService: BookmarkService
) {

    fun migrate() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val ringtonePrefKey = "pref_ringtone"
        val vibratorPrefKey = "pref_vibration"
        val ringtonePref = prefs.getString(ringtonePrefKey, null)
        val vibratorPref = prefs.getBoolean(vibratorPrefKey, false)
        if (ringtonePref != null) {
            context.getNotificationManager().deleteNotificationChannel("RandomTickerChannel:01")
            context.getNotificationManager().deleteNotificationChannel("RandomTickerChannel:03")
            bookmarkService.applyForAllBookmarks {
                val soundUri = kotlin.runCatching { Uri.parse(ringtonePref) }
                    .getOrNull() ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                context.createNotificationChannel(it, soundUri, vibratorPref)
            }
            prefs.edit {
                remove(ringtonePrefKey)
                remove(vibratorPrefKey)
            }
        }
    }
}
