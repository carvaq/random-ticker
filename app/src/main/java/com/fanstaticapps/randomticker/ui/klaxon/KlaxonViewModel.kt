package com.fanstaticapps.randomticker.ui.klaxon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take


class KlaxonViewModel(private val service: BookmarkService) : ViewModel() {
    fun getCurrentBookmark(bookmarkId: Long) = service.getBookmarkById(bookmarkId)
    fun needsRestartDueToAutoRepeat(bookmarkId: Long) = service.getBookmarkById(bookmarkId)
        .filter { it.autoRepeat }
        .onEach { delay(it.autoRepeatInterval) }
        .take(1)
        .asLiveData(viewModelScope.coroutineContext)

    fun cancelTimer(bookmark: Bookmark) {
        service.cancelTicker(bookmark.id)
    }

    fun scheduleTicker(bookmark: Bookmark) {
        service.scheduleAlarm(bookmark.id, true)
    }
}
