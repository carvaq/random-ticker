package com.cvv.fanstaticapps.randomticker.helper

import android.text.format.DateUtils

fun getFormattedElapsedMilliseconds(elapsedMilliseconds: Long): String {
    return DateUtils.formatElapsedTime(elapsedMilliseconds / TimerHelper.ONE_SECOND_IN_MILLIS)
}