package com.fanstaticapps.randomticker.ui.main

import androidx.lifecycle.ViewModel
import com.fanstaticapps.randomticker.data.Bookmark.Companion.NOT_SET_VALUE
import com.fanstaticapps.randomticker.data.BookmarkService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.seconds

class MainViewModel(private val bookmarkService: BookmarkService) : ViewModel(),
    MainTickerViewModel {
    private val runningTimers = flow {
        while (true) {
            delay(1.seconds)
            emit(System.currentTimeMillis())
        }
    }
    override val timers: Flow<TimersScreenUiState> = runningTimers.combine(
        bookmarkService.fetchAllBookmarks()
    ) { _, bookmarks ->
        bookmarks.map {
            TimerItemUiState(
                id = it.id,
                name = it.name,
                minInterval = it.min,
                maxInterval = it.max,
                autoRepeat = it.autoRepeat,
                alarmSound = it.soundUri,
                isRunning = it.intervalEnd != NOT_SET_VALUE,
                endTimeMillis = it.intervalEnd
            )
        }
    }.distinctUntilChanged()
        .map { TimersScreenUiState.Success(it) }


    override fun start(id: Long) {
        bookmarkService.scheduleAlarm(id, true)
    }

    override fun cancelTimer(id: Long) {
        bookmarkService.cancel(id)
    }

    override fun save(timerDetails: TimerItemUiState) {
        bookmarkService.save(timerDetails.toBookmark())
    }

    override fun delete(id: Long) {
        bookmarkService.delete(id)
    }
}

interface MainTickerViewModel {
    val timers: Flow<TimersScreenUiState>
    fun start(id: Long)
    fun cancelTimer(id: Long)
    fun save(timerDetails: TimerItemUiState)
    fun delete(id: Long)
}