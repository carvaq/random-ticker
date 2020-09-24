package com.fanstaticapps.randomticker.ui.bookmarks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarksViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle,
                                                      private val userPreferences: UserPreferences,
                                                      private val repository: BookmarkRepository) : ViewModel() {
    val allBookmarks = repository.getAllBookmarks()

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBookmark(bookmark)
        }
    }

}