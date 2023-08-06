package com.fanstaticapps.randomticker.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.data.IntervalDefinition
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val bookmarkService: BookmarkService,
    private val prefs: TickerPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val randomGenerator = Random()
    private val currentBookmarkId =
        MutableStateFlow(savedStateHandle.getBookmarkId(prefs.currentSelectedId))

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentBookmark: LiveData<Bookmark> = currentBookmarkId
        .filterNotNull()
        .onEach { prefs.currentSelectedId = it }
        .flatMapMerge { bookmarkService.getBookmarkById(it) }
        .asLiveData(viewModelScope.coroutineContext)

    fun createTicker(
        context: Context,
        bookmarkName: String,
        minimum: IntervalDefinition,
        maximum: IntervalDefinition,
        autoRepeat: Boolean
    ) {
        val interval = randomGenerator.nextInt((maximum - minimum + 1).toInt()) + minimum.millis
        val currentBookmark = Bookmark(
            name = bookmarkName,
            minimum = minimum,
            maximum = maximum,
            id = currentBookmarkId.value,
            autoRepeat = autoRepeat,
            intervalEnd = interval + System.currentTimeMillis()
        )

        viewModelScope.launch {
            currentBookmarkId.value =
                bookmarkService.createOrUpdate(context, currentBookmark).receive()
        }
    }

    fun cancelTicker(context: Context, bookmark: Bookmark) {
        bookmarkService.cancelTicker(context, bookmark.id)
    }
}