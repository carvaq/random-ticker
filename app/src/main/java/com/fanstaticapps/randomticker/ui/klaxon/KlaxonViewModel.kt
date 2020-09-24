package com.fanstaticapps.randomticker.ui.klaxon

import android.os.CountDownTimer
import androidx.annotation.CheckResult
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.extensions.getFormattedElapsedMilliseconds
import com.fanstaticapps.randomticker.helper.TimerHelper
import kotlin.math.abs

class KlaxonViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle,
                                                   private val userPreferences: UserPreferences,
                                                   repository: BookmarkRepository) : ViewModel() {

    private val timeElapsed = MutableLiveData<Boolean>()
    private val internalViewState = MutableLiveData<KlaxonViewState>()
    private val viewStateMediator = MediatorLiveData<KlaxonViewState>()
    private val countDownTimer = object : CountDownTimer(userPreferences.intervalFinished - System.currentTimeMillis(),
            TimerHelper.ONE_SECOND_IN_MILLIS) {
        override fun onTick(millisUntilFinished: Long) {
            if (showElapsedTime) {
                internalViewState.value = KlaxonViewState.ElapseTimeUpdate(getElapsedTime())
            }
        }

        override fun onFinish() {
            internalViewState.value = timerFinished()
        }
    }


    val currentBookmark: LiveData<Bookmark> = repository.getBookmarkById(userPreferences.currentSelectedId)
    var showElapsedTime: Boolean = true

    init {
        viewStateMediator.addSource(Transformations.map(timeElapsed) { timerElapsed ->
            if (timerElapsed) {
                timerFinished()
            } else {
                startCountDownTimer()
                KlaxonViewState.TimerStarted
            }
        }) { viewStateMediator.value = it }
        viewStateMediator.addSource(internalViewState) { viewStateMediator.value = it }
    }


    fun setTimeElapsed(timerElapsed: Boolean) {
        this.timeElapsed.value = timerElapsed
    }

    fun getCurrentViewState(): LiveData<KlaxonViewState> {
        return viewStateMediator
    }

    fun onStop() {
        countDownTimer.cancel()
        internalViewState.value = KlaxonViewState.TimerStopped
    }

    fun cancelTimer() {
        countDownTimer.cancel()
        internalViewState.value = KlaxonViewState.TimerCanceled
    }

    private fun startCountDownTimer() {
        countDownTimer.start()
    }

    private fun getElapsedTime(): String {
        val millisSinceStarted = abs(userPreferences.intervalFinished - System.currentTimeMillis() - userPreferences.interval)
        return getFormattedElapsedMilliseconds(millisSinceStarted)
    }

    @CheckResult
    private fun timerFinished(): KlaxonViewState.TimerFinished {
        userPreferences.currentlyTickerRunning = false
        return KlaxonViewState.TimerFinished(getElapsedTime())
    }

}