package com.fanstaticapps.randomticker.data

data class IntervalDefinition(val hours: Int, val minutes: Int, val seconds: Int) {

    val millis = (60 * 60 * hours + 60 * minutes + seconds) * 1000

}