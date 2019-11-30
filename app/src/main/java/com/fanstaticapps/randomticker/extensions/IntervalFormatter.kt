package com.fanstaticapps.randomticker.extensions

import android.text.format.DateUtils
import com.fanstaticapps.randomticker.helper.TimerHelper

fun getFormattedElapsedMilliseconds(elapsedMilliseconds: Long): String {
    return DateUtils.formatElapsedTime(elapsedMilliseconds / TimerHelper.ONE_SECOND_IN_MILLIS)
}