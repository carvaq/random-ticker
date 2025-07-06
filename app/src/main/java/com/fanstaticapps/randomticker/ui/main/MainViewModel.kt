package com.fanstaticapps.randomticker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.data.BookmarkService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainViewModel(private val bookmarkService: BookmarkService) : ViewModel(),
    MainTickerViewModel {
    private val runningTimers = flow {
        while (true) {
            delay(1.seconds)
            emit(System.currentTimeMillis())
        }
    }
    override val timers: Flow<List<TimerItemUiState>> = runningTimers.combine(
        bookmarkService.fetchAllBookmarks()
    ) { tick, bookmarks ->
        bookmarks.map {
            TimerItemUiState(
                id = it.id,
                name = it.name,
                minInterval = it.min,
                maxInterval = it.max,
                autoRepeat = it.autoRepeat,
                alarmSound = it.soundUri,
                isRunning = it.intervalEnd > tick,
                endTimeMillis = it.intervalEnd
            )
        }
    }.distinctUntilChanged()


    override fun start(id: Long) {
        bookmarkService.scheduleAlarm(id, true)
    }

    override fun cancelTimer(id: Long) {
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
    fun cancelTimer(id: Long)
    fun save(timerDetails: TimerItemUiState)
    fun delete(id: Long)
}