package com.fanstaticapps.randomticker.helper

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.media.RingtoneManager.getDefaultUri
import androidx.core.content.edit
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getNotificationManager

class MigrationService(
    private val context: Context,
    private val bookmarkService: BookmarkService,
) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun hasMigrationRun(version: Int): Boolean {
        return sharedPreferences.getInt(LAST_MIGRATION_VERSION_KEY, 0) >= version
    }

    private fun markMigrationAsRun(version: Int) {
        sharedPreferences.edit { putInt(LAST_MIGRATION_VERSION_KEY, version) }
    }

    fun runMigrationIfNeeded() {
        if (!hasMigrationRun(TARGET_MIGRATION_VERSION)) {
            migrateSoundSettings()
            markMigrationAsRun(TARGET_MIGRATION_VERSION)
        }
    }

    private fun migrateSoundSettings() {
        val manager = context.getNotificationManager()
        bookmarkService.updateBookmarks { bookmark ->
            if (bookmark.soundUri.isNullOrEmpty()) {
                val channel = manager.getNotificationChannel("${bookmark.id}-KLAXON")
                channel?.id?.let { manager.deleteNotificationChannel(it) }
                val uri = channel?.sound ?: getDefaultUri(RingtoneManager.TYPE_ALARM)
                bookmark.copy(soundUri = uri.toString())
            } else {
                bookmark
            }
        }
    }


    companion object {
        private const val PREFS_NAME = "migration_prefs"
        const val LAST_MIGRATION_VERSION_KEY = "last_migration_version"
        const val TARGET_MIGRATION_VERSION = 1
    }
}
