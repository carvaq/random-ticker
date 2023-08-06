package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import timber.log.Timber
import javax.inject.Inject

abstract class BaseReceiver : BroadcastReceiver() {
    @Inject
    lateinit var tickerNotificationManager: TickerNotificationManager

    @Inject
    internal lateinit var bookmarkService: BookmarkService

    override fun onReceive(context: Context, intent: Intent) {
        val bookmarkId = intent.getBookmarkId()
        if (bookmarkId != null) {
            Timber.d("Bookmark found")
            handleBookmark(context, intent, bookmarkId)
        } else {
            Timber.e("No bookmark ID passed")
        }
    }

    abstract fun handleBookmark(context: Context, intent: Intent, bookmarkId: Long)
}