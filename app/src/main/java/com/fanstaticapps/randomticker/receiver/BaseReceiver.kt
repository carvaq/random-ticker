package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import timber.log.Timber
import javax.inject.Inject

abstract class BaseReceiver : BroadcastReceiver() {

    @Inject
    internal lateinit var bookmarkService: BookmarkService

    override fun onReceive(context: Context, intent: Intent) {
        val bookmarkId = intent.getBookmarkId()
        if (bookmarkId != null) {
            Timber.d("Bookmark found")
            handleBookmark(context, bookmarkId)
        } else {
            Timber.e("No bookmark ID passed")
        }
    }

    abstract fun handleBookmark(context: Context, bookmarkId: Long)
}