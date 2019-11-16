package com.fanstaticapps.randomticker.helper

import android.text.format.DateUtils
import com.fanstaticapps.randomticker.TimerHelper

fun getFormattedElapsedMilliseconds(elapsedMilliseconds: Long): String {
    return DateUtils.formatElapsedTime(elapsedMilliseconds / TimerHelper.ONE_SECOND_IN_MILLIS)
}