package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

abstract class BaseReceiver : BroadcastReceiver() {
    private val wrapper: BroadcastWrapper by lazy { BroadcastWrapper() }
    override fun onReceive(context: Context, intent: Intent) {
        val bookmarkId = intent.getBookmarkId()
        if (bookmarkId != null) {
            Timber.d("Bookmark found")
            wrapper.handleBookmark(context, bookmarkId)
        } else {
            Timber.e("No bookmark ID passed")
        }
    }

    abstract fun BroadcastWrapper.handleBookmark(context: Context, bookmarkId: Long)

    class BroadcastWrapper : KoinComponent {
        internal val bookmarkService: BookmarkService by inject()
    }
}