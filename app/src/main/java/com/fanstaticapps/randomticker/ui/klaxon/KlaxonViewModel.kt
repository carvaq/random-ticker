package com.fanstaticapps.randomticker.ui.klaxon

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import kotlinx.coroutines.flow.firstOrNull


class KlaxonViewModel(
    savedStateHandle: SavedStateHandle,
    private val service: BookmarkService
) : ViewModel() {

    private val bookmarkId = savedStateHandle.getBookmarkId()

    val currentBookmark = liveData {
        emit(bookmarkId?.let { service.getBookmarkById(it).firstOrNull() })
    }

    fun cancelTimer(context: Context) {
        bookmarkId?.let { service.cancelTicker(context, it) }
    }

    fun scheduleTicker(context: Context) {
        bookmarkId?.let { service.scheduleAlarm(context, it, true) }
    }
}
