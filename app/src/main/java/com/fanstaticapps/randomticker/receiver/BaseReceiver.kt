package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import timber.log.Timber

abstract class BaseReceiver : BroadcastReceiver(), KoinComponent {
    protected val bookmarkService: BookmarkService by inject()

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

    override fun getKoin(): Koin = get()
}