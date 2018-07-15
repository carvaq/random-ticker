package com.cvv.fanstaticapps.randomticker.mvp

import android.os.CountDownTimer
import com.cvv.fanstaticapps.randomticker.PREFS
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper
import com.cvv.fanstaticapps.randomticker.helper.getFormattedElapsedMilliseconds
import java.lang.Math.abs

/**
 * @intervalFinished timestamp when the timer should ring
 */
class KlaxonPresenter(private val view: KlaxonView, private val intervalFinished: Long, private val timeElapsed: Boolean) {
    private var countDownTimer: CountDownTimer? = null
    var showElapsedTime: Boolean = false

    fun init() {
        if (timeElapsed) {
            timerFinished()
        } else {
            startCountDownTimer()
            view.render(ViewState.TimerStarted())
        }
    }

    fun update(timeElapsed: Boolean) {
        if (timeElapsed) {
            timerFinished()
        }
    }

    fun cancelTimer() {
        countDownTimer?.cancel()
        view.render(ViewState.TimerCanceled())
    }

    fun onStop() {
        countDownTimer?.cancel()
        view.render(ViewState.TimerStopped())

    }

    private fun timerFinished() {
        val state = ViewState.TimerFinished(getElapsedTime())
        view.render(state)
    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(intervalFinished - System.currentTimeMillis(), TimerHelper.ONE_SECOND_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                if (showElapsedTime) {
                    view.render(ViewState.ElapseTimeUpdate(getElapsedTime()))
                }
            }

            override fun onFinish() {
                timerFinished()
                PREFS.currentlyTickerRunning = false
                countDownTimer = null
            }
        }
        countDownTimer!!.start()
    }

    private fun getElapsedTime(): String {
        val millisSinceStarted = abs(intervalFinished - System.currentTimeMillis() - PREFS.interval)
        return getFormattedElapsedMilliseconds(millisSinceStarted)
    }

    sealed class ViewState {
        data class ElapseTimeUpdate(val elapsedTime: String?) : ViewState()
        data class TimerFinished(val elapsedTime: String) : ViewState()
        class TimerStarted() : ViewState()
        class TimerCanceled() : ViewState()
        class TimerStopped() : ViewState()
    }
}