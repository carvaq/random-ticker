package com.fanstaticapps.randomticker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainViewModel(private val bookmarkService: BookmarkService) :
    ViewModel() {
    private val selectedBookmarkId = MutableStateFlow<Long?>(null)
    val bookmarks = bookmarkService.fetchAllBookmarks()
    val selectedBookmark = combine(
        selectedBookmarkId,
        bookmarks
    ) { selectedBookmarkId, bookmarks ->
        bookmarks.find { it.id == selectedBookmarkId }
    }

    fun startBookmark(bookmark: Bookmark) {
        bookmarkService.scheduleAlarm(bookmark.id, true)
    }

    fun select(bookmarkId: Long?) {
        selectedBookmarkId.value = bookmarkId
    }

    fun createNewBookmark() {
        viewModelScope.launch {
            selectedBookmarkId.emit(bookmarkService.createNew())
        }
    }

    fun cancelTicker(bookmarkId: Long) {
        bookmarkService.cancelTicker(bookmarkId)
    }

    fun save(bookmark: Bookmark) {
        bookmarkService.save(bookmark)
    }

    fun delete(bookmark: Bookmark) {
        bookmarkService.delete(bookmark)
    }
}
