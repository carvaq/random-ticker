package com.fanstaticapps.randomticker.ui.klaxon

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.extensions.EXTRA_BOOKMARK_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class KlaxonViewModel @Inject constructor(
    tickerPreferences: TickerPreferences,
    savedStateHandle: SavedStateHandle,
    repository: BookmarkRepository
) : ViewModel() {
    private val bookmarkId =
        savedStateHandle[EXTRA_BOOKMARK_ID] ?: tickerPreferences.currentSelectedId

    val currentBookmark: LiveData<Bookmark> =
        repository.getBookmarkById(bookmarkId)
            .flowOn(Dispatchers.IO)
            .asLiveData(viewModelScope.coroutineContext)

}