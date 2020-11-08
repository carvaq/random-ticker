package com.fanstaticapps.randomticker.ui.bookmarks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import kotlinx.coroutines.launch

internal class BookmarksViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle,
                                                               private val tickerPreferences: TickerPreferences,
                                                               private val repository: BookmarkRepository) : ViewModel() {

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