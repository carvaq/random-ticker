package com.fanstaticapps.randomticker.ui.cancel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getBookmarkId

class CancelViewModel(
    savedStateHandle: SavedStateHandle,
    private val service: BookmarkService
) : ViewModel() {
    private val bookmarkId = savedStateHandle.getBookmarkId()

    fun cancelTicker() {
        bookmarkId?.let { service.cancelTicker(it) }
    }

}
