package com.fanstaticapps.randomticker.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.data.IntervalDefinition
import com.fanstaticapps.randomticker.extensions.EXTRA_BOOKMARK_ID
import com.fanstaticapps.randomticker.helper.TimerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val tickerPreferences: TickerPreferences,
    private val timerHelper: TimerHelper,
    private val repository: BookmarkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val randomGenerator = Random()
    private val bookmarkId =
        savedStateHandle[EXTRA_BOOKMARK_ID] ?: tickerPreferences.currentSelectedId

    val currentBookmark: LiveData<Bookmark> = repository.getBookmarkById(bookmarkId)
        .flowOn(Dispatchers.IO)
        .asLiveData(viewModelScope.coroutineContext)

    fun createTimer(
        bookmarkName: String,
        minimum: IntervalDefinition,
        maximum: IntervalDefinition,
        autoRepeat: Boolean
    ): Boolean {
        val valid = minimum < maximum
        if (valid) {
            createOrUpdateBookmark(bookmarkName, minimum, maximum, autoRepeat)
        }
        return valid
    }

    private fun createOrUpdateBookmark(
        name: String,
        minimum: IntervalDefinition,
        maximum: IntervalDefinition,
        autoRepeat: Boolean
    ) {
        val interval = randomGenerator.nextInt((maximum - minimum + 1).toInt()) + minimum.millis
        val currentBookmark = Bookmark(
            name = name,
            minimum = minimum,
            maximum = maximum,
            id = tickerPreferences.currentSelectedId,
            autoRepeat = autoRepeat,
            intervalEnd = interval + System.currentTimeMillis()
        )

        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insertOrUpdateBookmark(currentBookmark)
            tickerPreferences.currentSelectedId = id
            timerHelper.startTicker(currentBookmark)
        }
    }
}