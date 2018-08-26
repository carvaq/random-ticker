package com.fanstaticapps.sequentialtimer

sealed class Condition {
    data class ExactInterval(val minutes: Int, val seconds: Int)
        : Condition()

    data class NumberOfTimes(val count: Int) : Condition()
    data class RandomInterval(val minMinutes: Int, val minSeconds: Int,
                              val maxMinutes: Int, val maxSeconds: Int) : Condition()

    object Or : Condition()
    object And : Condition()
}

enum class Operator {
    AND, OR
}
