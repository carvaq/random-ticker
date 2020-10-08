package com.fanstaticapps.randomticker.ui.klaxon

import android.os.CountDownTimer
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.extensions.getFormattedElapsedMilliseconds
import com.fanstaticapps.randomticker.helper.TimerHelper
import kotlinx.coroutines.Dispatchers
import kotlin.math.abs

class KlaxonViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle,
                                                   private val tickerPreferences: TickerPreferences,
                                                   repository: BookmarkRepository) : ViewModel() {

    private val timeElapsed = MutableLiveData<Boolean>()
    private val internalViewState = MutableLiveData<KlaxonViewState>(KlaxonViewState.TickerNoop)
    private val viewStateMediator = MediatorLiveData<KlaxonViewState>()

    private val countDownTimer = object : CountDownTimer(tickerPreferences.intervalWillBeFinished - System.currentTimeMillis(),
            TimerHelper.ONE_SECOND_IN_MILLIS) {
        override fun onTick(millisUntilFinished: Long) {
            if (showElapsedTime) {
                internalViewState.value = KlaxonViewState.ElapsedTimeUpdate(getElapsedTime())
            }
        }

        override fun onFinish() {
            timeElapsed.value = true
        }
    }


    val currentBookmark: LiveData<Bookmark> = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
        repository.getBookmarkById(tickerPreferences.currentSelectedId)?.let { emit(it) }
    }
    var showElapsedTime: Boolean = false

    init {
        viewStateMediator.addSource(Transformations.switchMap(timeElapsed) { timerElapsed ->
            Transformations.map(currentBookmark) { bookmark ->
                if (timerElapsed) {
                    timerFinished(bookmark)
                } else {
                    startCountDownTimer()
                    KlaxonViewState.TickerStarted(bookmark)
                }
            }
        }) { viewStateMediator.value = it }
        viewStateMediator.addSource(internalViewState) { viewStateMediator.value = it }
    }

    private fun timerFinished(bookmark: Bookmark): KlaxonViewState {
        return if (bookmark.autoRepeat) {
            KlaxonViewState.TickerRepeat(bookmark)
        } else {
            KlaxonViewState.TickerFinished(getElapsedTime(), bookmark)
        }
    }


    fun setTimeElapsed(timerElapsed: Boolean) {
        this.timeElapsed.value = timerElapsed
    }

    fun getCurrentViewState(): LiveData<KlaxonViewState> {
        return viewStateMediator
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
        val millisSinceStarted = abs(tickerPreferences.intervalWillBeFinished - System.currentTimeMillis() - tickerPreferences.interval)
        return getFormattedElapsedMilliseconds(millisSinceStarted)
    }

}