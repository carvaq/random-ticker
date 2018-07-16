package com.fanstaticapps.common.helper

import android.text.format.DateUtils
import com.fanstaticapps.common.TimerHelper

fun getFormattedElapsedMilliseconds(elapsedMilliseconds: Long): String {
    return DateUtils.formatElapsedTime(elapsedMilliseconds / TimerHelper.ONE_SECOND_IN_MILLIS)
}