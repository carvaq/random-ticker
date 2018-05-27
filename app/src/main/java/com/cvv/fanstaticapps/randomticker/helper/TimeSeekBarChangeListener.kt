package com.cvv.fanstaticapps.randomticker.helper

import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import java.lang.Math.exp
import java.lang.Math.log
import java.util.*

/**
 * Created by Carla
 * Date: 04/02/2018
 * Project: RandomTicker
 */

class TimeSeekBarChangeListener(private val displayTextView: TextView,
                                private val minuteSeekbar: SeekBar,
                                private val secondsSeekbar: SeekBar) : SeekBar.OnSeekBarChangeListener {

    var minutes: Int = 0
    var seconds: Int = 0

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val minutesProgress = minuteSeekbar.progress
        //Formula from https://plus.google.com/+AladinQ/posts/2B1fpKQWFmG
        minutes = exp(LOG_MIN_VALUE + (minutesProgress - MIN_VALUE) * FACTOR).toInt()
        seconds = secondsSeekbar.progress
        val displayText = String.format(Locale.getDefault(), TIME_FORMAT, minutes, seconds)
        displayTextView.text = displayText
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    companion object {

        private val MIN_VALUE = 1
        private val MAX_VALUE = 59
        private val LOG_MIN_VALUE = log(MIN_VALUE.toDouble())
        private val LOG_MAX_VALUE = log(MAX_VALUE.toDouble())
        private val FACTOR = (LOG_MAX_VALUE - LOG_MIN_VALUE) / (MAX_VALUE - MIN_VALUE)

        @VisibleForTesting
        val TIME_FORMAT = "%02dm %02ds"
    }
}
