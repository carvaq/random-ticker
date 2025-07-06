package com.fanstaticapps.randomticker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.data.BookmarkService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel(private val bookmarkService: BookmarkService) : ViewModel(),
    MainTickerViewModel {
    override val timers: Flow<List<TimerItemUiState>> =
        bookmarkService.fetchAllBookmarks().map { bookmarks ->
            bookmarks.map {
                TimerItemUiState(
                    it.id,
                    it.name,
                    it.min,
                    it.max,
                    it.intervalEnd,
                    it.autoRepeat,
                    it.soundUri
                )
            }
    }

    override fun start(id: Long) {
        bookmarkService.scheduleAlarm(id, true)
    }

    override fun cancelTicker(id: Long) {
        bookmarkService.cancelTimer(id)
    }

    override fun save(timerDetails: TimerItemUiState) {
        bookmarkService.save(timerDetails.toBookmark())
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            bookmarkService.getBookmarkById(id).firstOrNull()?.let {
                bookmarkService.delete(it)
            }
        }
    }
}

interface MainTickerViewModel {
    val timers: Flow<List<TimerItemUiState>>
    fun start(id: Long)
    fun cancelTicker(id: Long)
    fun save(timerDetails: TimerItemUiState)
    fun delete(id: Long)
}