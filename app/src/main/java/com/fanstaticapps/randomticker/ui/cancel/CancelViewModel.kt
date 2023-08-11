package com.fanstaticapps.randomticker.ui.cancel

import androidx.lifecycle.ViewModel
import com.fanstaticapps.randomticker.data.BookmarkService

class CancelViewModel(private val service: BookmarkService) : ViewModel() {
    fun cancelTicker(bookmarkId: Long?) {
        bookmarkId?.let { service.cancelTicker(it) }
    }
}
