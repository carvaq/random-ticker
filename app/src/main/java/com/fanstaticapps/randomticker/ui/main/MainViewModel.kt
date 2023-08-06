package com.fanstaticapps.randomticker.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.data.IntervalDefinition
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import java.util.Random
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val bookmarkService: BookmarkService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val randomGenerator = Random()
    private val bookmarkId = MutableStateFlow(savedStateHandle.getBookmarkId())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentBookmark: LiveData<Bookmark> = bookmarkId
        .filterNotNull()
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
            id = currentBookmark.value?.id,
            autoRepeat = autoRepeat,
            intervalEnd = interval + System.currentTimeMillis()
        )

        bookmarkService.createOrUpdate(context, currentBookmark)
    }

    fun cancelTicker(context: Context, bookmark: Bookmark) {
        bookmarkService.cancelTicker(context, bookmark.id)
    }
}