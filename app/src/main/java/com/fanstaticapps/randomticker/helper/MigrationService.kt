package com.fanstaticapps.randomticker.helper

import android.content.Context
import android.media.RingtoneManager
import android.media.RingtoneManager.getDefaultUri
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getNotificationManager

class MigrationService(
    private val context: Context,
    private val bookmarkService: BookmarkService,
) {

    fun migrate() {
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
}
