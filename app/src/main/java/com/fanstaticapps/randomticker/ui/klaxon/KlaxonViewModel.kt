package com.fanstaticapps.randomticker.ui.klaxon

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.EXTRA_BOOKMARK_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KlaxonViewModel @Inject constructor(
    tickerPreferences: TickerPreferences,
    savedStateHandle: SavedStateHandle,
    private val service: BookmarkService
) : ViewModel() {
    private val bookmarkId =
        savedStateHandle[EXTRA_BOOKMARK_ID] ?: tickerPreferences.currentSelectedId

    val currentBookmark = service.getBookmarkById(bookmarkId)
        .asLiveData(viewModelScope.coroutineContext)

    fun cancelTimer(context: Context) {
        service.cancelTicker(context, bookmarkId)
    }

    fun scheduleTicker(context: Context) {
        service.scheduleAlarm(context, bookmarkId, true)
    }
}
