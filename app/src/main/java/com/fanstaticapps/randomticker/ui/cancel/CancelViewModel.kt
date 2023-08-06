package com.fanstaticapps.randomticker.ui.cancel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CancelViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val service: BookmarkService
) : ViewModel() {
    private val bookmarkId = savedStateHandle.getBookmarkId()

    fun cancelTimer(context: Context) {
        bookmarkId?.let { service.cancelAlarm(context, it) }
    }

}
