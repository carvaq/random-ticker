package com.fanstaticapps.randomticker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainViewModel(private val bookmarkService: BookmarkService) :
    ViewModel(), MainTickerViewModel {
    private val selectedBookmarkId = MutableStateFlow<Long?>(null)
    val bookmarks = bookmarkService.fetchAllBookmarks()
    val selectedBookmark = combine(
        selectedBookmarkId,
        bookmarks
    ) { selectedBookmarkId, bookmarks ->
        bookmarks.find { it.id == selectedBookmarkId }
    }

    override fun startBookmark(bookmark: Bookmark) {
        bookmarkService.scheduleAlarm(bookmark.id, true)
    }

    override fun select(bookmarkId: Long?) {
        selectedBookmarkId.value = bookmarkId
    }

    override fun createNewBookmark() {
        viewModelScope.launch {
            selectedBookmarkId.emit(bookmarkService.createNew())
        }
    }

    override fun cancelTicker(bookmarkId: Long) {
        bookmarkService.cancelTicker(bookmarkId)
    }

    override fun save(bookmark: Bookmark) {
        bookmarkService.save(bookmark)
    }

    override fun delete(bookmark: Bookmark) {
        bookmarkService.delete(bookmark)
    }
}

interface MainTickerViewModel {
    fun startBookmark(bookmark: Bookmark)
    fun select(bookmarkId: Long?)
    fun createNewBookmark()
    fun cancelTicker(bookmarkId: Long)
    fun save(bookmark: Bookmark)
    fun delete(bookmark: Bookmark)
}