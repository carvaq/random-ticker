package com.fanstaticapps.randomticker.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class BookmarksViewModel @Inject constructor(
    private val tickerPreferences: TickerPreferences,
    private val repository: BookmarkRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    val allBookmarks = repository.getAllBookmarks().asLiveData(viewModelScope.coroutineContext)

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch(dispatcher) {
            repository.deleteBookmark(bookmark)
        }
    }

    fun createBookmark(newBookmark: Bookmark) {
        viewModelScope.launch(dispatcher) {
            val id = repository.insertOrUpdateBookmark(newBookmark)
            tickerPreferences.currentSelectedId = id
        }
    }

    fun selectBookmark(bookmark: Bookmark) {
        tickerPreferences.currentSelectedId = bookmark.id ?: 0
    }

}