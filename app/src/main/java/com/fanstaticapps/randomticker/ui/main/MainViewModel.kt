package com.fanstaticapps.randomticker.ui.main

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.data.IntervalDefinition
import com.fanstaticapps.randomticker.helper.TimerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle,
                                                 private val userPreferences: UserPreferences,
                                                 private val timerHelper: TimerHelper,
                                                 private val repository: BookmarkRepository) : ViewModel() {

    private val currentBookmarkId = MutableLiveData(userPreferences.currentSelectedId)

    val timerCreationStatus = MutableLiveData<TimerCreationStatus>()
    val currentBookmark: LiveData<Bookmark> = Transformations.switchMap(currentBookmarkId) { currentBookmarkId ->
        repository.getBookmarkById(currentBookmarkId)
    }

    fun setCurrentBookmark(bookmark: Bookmark) {
        currentBookmarkId.value = bookmark.id
    }

    fun createTimer(bookmarkName: String, minimum: IntervalDefinition, maximum: IntervalDefinition) {
        val timerCreated = timerHelper.createTimer(minimum, maximum)
        if (timerCreated) {
            createOrUpdateBookmark(bookmarkName, minimum, maximum)

            timerCreationStatus.value = TimerCreationStatus.TIMER_STARTED
        } else {
            timerCreationStatus.value = TimerCreationStatus.INVALID_DATES
        }

    }

    private fun createOrUpdateBookmark(name: String, minimum: IntervalDefinition, maximum: IntervalDefinition) {
        val newBookmark = Bookmark(name = name, minimum = minimum, maximum = maximum)

        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insertBookmark(newBookmark)
            userPreferences.currentSelectedId = id

        }
    }
}