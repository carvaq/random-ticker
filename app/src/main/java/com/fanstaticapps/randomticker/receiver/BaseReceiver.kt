package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import kotlinx.coroutines.Job
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

abstract class BaseReceiver : BroadcastReceiver() {
    private val wrapper: BroadcastWrapper by lazy { BroadcastWrapper() }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val bookmarkId = intent.getBookmarkId()
        if (bookmarkId != null) {
            Timber.d("Bookmark found")
            val pendingResult = goAsync()
            val job = wrapper.handleBookmark(bookmarkId)
            job?.invokeOnCompletion { pendingResult.finish() } ?: pendingResult.finish()
        } else {
            Timber.e("No bookmark ID passed")
        }
    }

    abstract fun BroadcastWrapper.handleBookmark(
        bookmarkId: Long,
    ): Job?

    class BroadcastWrapper : KoinComponent {
        internal val bookmarkService: BookmarkService by inject()
    }
}