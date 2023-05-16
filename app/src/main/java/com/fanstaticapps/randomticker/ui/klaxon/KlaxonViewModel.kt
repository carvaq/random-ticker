package com.fanstaticapps.randomticker.ui.klaxon

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.extensions.getFormattedElapsedMilliseconds
import com.fanstaticapps.randomticker.helper.TimerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class KlaxonViewModel @Inject constructor(
    private val tickerPreferences: TickerPreferences,
    repository: BookmarkRepository
) : ViewModel() {

    private val timeElapsed = MutableLiveData<Boolean>()
    private val internalViewState = MutableLiveData<KlaxonViewState>(KlaxonViewState.TickerNoop)
    val currentStateLD: LiveData<KlaxonViewState> = MediatorLiveData<KlaxonViewState>().apply {
        addSource(timeElapsed.switchMap { timerElapsed ->
            currentBookmark.map { bookmark ->
                if (timerElapsed) {
                    KlaxonViewState.TickerFinished(getElapsedTime(), bookmark)
                } else {
                    startCountDownTimer()
                    KlaxonViewState.TickerStarted(bookmark)
                }
            }
        }) { value = it }
        addSource(internalViewState) { value = it }
    }

    private val countDownTimer = object : CountDownTimer(
        tickerPreferences.intervalWillBeFinished - System.currentTimeMillis(),
        TimerHelper.ONE_SECOND_IN_MILLIS
    ) {
        override fun onTick(millisUntilFinished: Long) {
            if (showElapsedTime) {
                internalViewState.value = KlaxonViewState.ElapsedTimeUpdate(getElapsedTime())
            }
        }

        override fun onFinish() {
            timeElapsed.value = true
        }
    }


    val currentBookmark: LiveData<Bookmark> =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            repository.getBookmarkById(tickerPreferences.currentSelectedId)?.let { emit(it) }
        }
    var showElapsedTime: Boolean = false


    fun setTimeElapsed(timerElapsed: Boolean) {
        this.timeElapsed.value = timerElapsed
    }

    fun onStop() {
        countDownTimer.cancel()
        internalViewState.value = KlaxonViewState.TickerStopped
    }

    fun cancelTimer() {
        countDownTimer.cancel()
        internalViewState.value = KlaxonViewState.TickerCanceled
    }

    private fun startCountDownTimer() {
        countDownTimer.start()
    }

    private fun getElapsedTime(): String {
        val millisSinceStarted =
            abs(tickerPreferences.intervalWillBeFinished - System.currentTimeMillis() - tickerPreferences.interval)
        return getFormattedElapsedMilliseconds(millisSinceStarted)
    }

}