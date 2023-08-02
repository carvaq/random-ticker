package com.fanstaticapps.randomticker.ui.klaxon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.ui.main.MainActivity.Companion.EXTRA_BOOKMARK_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class KlaxonViewModel @Inject constructor(
    tickerPreferences: TickerPreferences,
    savedStateHandle: SavedStateHandle,
    repository: BookmarkRepository
) : ViewModel() {
    private val bookmarkId =
        savedStateHandle[EXTRA_BOOKMARK_ID] ?: tickerPreferences.currentSelectedId
    private val timeElapsed = MutableLiveData<Boolean>()


    val currentBookmark: LiveData<Bookmark> =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            repository.getBookmarkById(bookmarkId)?.let { emit(it) }
        }


    fun setTimeElapsed(timerElapsed: Boolean) {
        this.timeElapsed.value = timerElapsed
    }
}