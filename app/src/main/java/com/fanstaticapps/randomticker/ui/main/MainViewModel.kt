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

    private val currentBookmarkId = userPreferences.currentSelectedBookmarkIdAsLiveData

    val timerCreationStatus = MutableLiveData<TimerCreationStatus>()
    val currentBookmark: LiveData<Bookmark> = Transformations.switchMap(currentBookmarkId) { currentBookmarkId ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            repository.getBookmarkById(currentBookmarkId)?.let { emit(it) }
        }
    }

    fun createTimer(bookmarkName: String, minimum: IntervalDefinition, maximum: IntervalDefinition) {
        val timerCreated = timerHelper.createTicker(minimum, maximum)
        if (timerCreated) {
            createOrUpdateBookmark(bookmarkName, minimum, maximum)

            timerCreationStatus.value = TimerCreationStatus.TIMER_STARTED
        } else {
            timerCreationStatus.value = TimerCreationStatus.INVALID_DATES
        }

    }

    private fun createOrUpdateBookmark(name: String, minimum: IntervalDefinition, maximum: IntervalDefinition) {
        val currentBookmark = Bookmark(name = name, minimum = minimum, maximum = maximum, id = userPreferences.currentSelectedId)

        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insertOrUpdateBookmark(currentBookmark)
            userPreferences.currentSelectedId = id
        }
    }
}