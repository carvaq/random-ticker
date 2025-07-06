package com.fanstaticapps.randomticker.helper

import android.content.Context
import android.media.RingtoneManager
import android.media.RingtoneManager.getDefaultUri
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.createNotificationChannel

class MigrationService(
    private val context: Context,
    private val bookmarkService: BookmarkService,
) {
    
    fun migrate() {
        bookmarkService.updateBookmarks { bookmark ->
            if (bookmark.soundUri.isNullOrEmpty()) {
                val uri = context.createNotificationChannel(bookmark)?.sound ?: getDefaultUri(RingtoneManager.TYPE_ALARM)
                bookmark.copy(soundUri = uri.toString())
            } else {
                bookmark
            }
        }
    }
}
