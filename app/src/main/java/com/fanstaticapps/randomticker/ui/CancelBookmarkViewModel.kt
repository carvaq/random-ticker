package com.fanstaticapps.randomticker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.helper.TimerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CancelBookmarkViewModel @Inject constructor(
    private val timerHelper: TimerHelper,
    private val repository: BookmarkRepository
) : ViewModel() {

    fun cancelTimer(bookmarkId: Long?) {
        bookmarkId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBookmarkById(bookmarkId).firstOrNull()?.let { bookmark ->
                repository.insertOrUpdateBookmark(bookmark.reset())
            }
            timerHelper.cancelTicker(bookmarkId)
        }
    }
}