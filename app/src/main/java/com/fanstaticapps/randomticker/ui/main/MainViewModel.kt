package com.fanstaticapps.randomticker.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val bookmarkService: BookmarkService) :
    ViewModel() {
    val bookmarks = bookmarkService.fetchAllBookmarks()
    val createdBookmarkId = MutableStateFlow<Long?>(null)

    fun startBookmark(context: Context, bookmark: Bookmark) {
        bookmarkService.scheduleAlarm(context, bookmark.id, true)
    }

    fun createNewBookmark() {
        viewModelScope.launch {
            createdBookmarkId.emit(bookmarkService.createNew())
        }
    }
}
