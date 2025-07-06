package com.fanstaticapps.randomticker.helper

import android.content.Context
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.createNotificationChannel

class MigrationService(
    private val context: Context,
    private val bookmarkService: BookmarkService
) {

    fun migrate() {
        bookmarkService.applyForAllBookmarks { bookmark ->
            context.createNotificationChannel(bookmark)?.let {
                bookmark.copy(soundUri = it.sound.toString())
            }
        }
    }
}
