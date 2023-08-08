package com.fanstaticapps.randomticker.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService

class MainViewModel(private val bookmarkService: BookmarkService) :
    ViewModel() {
    fun startBookmark(context: Context, bookmark: Bookmark) {
        bookmarkService.scheduleAlarm(context, bookmark.id, true)
    }

    val bookmarks = bookmarkService.fetchAllBookmarks()
}
