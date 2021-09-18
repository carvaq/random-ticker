package com.fanstaticapps.randomticker.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class BookmarksViewModel @Inject constructor(
    private val tickerPreferences: TickerPreferences,
    private val repository: BookmarkRepository
) : ViewModel() {

    val allBookmarks = repository.getAllBookmarks()

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmark)
        }
    }

    fun createBookmark(newBookmark: Bookmark) {
        viewModelScope.launch {
            val id = repository.insertOrUpdateBookmark(newBookmark)
            tickerPreferences.currentSelectedId = id
        }
    }

    fun selectBookmark(bookmark: Bookmark) {
        tickerPreferences.currentSelectedId = bookmark.id ?: 0
    }

}