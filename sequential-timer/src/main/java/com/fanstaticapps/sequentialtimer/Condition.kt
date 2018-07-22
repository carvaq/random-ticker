package com.fanstaticapps.sequentialtimer

sealed class Condition(val conditionOperator: Operator) {
    data class ExactInterval(val operator: Operator, val minutes: Int, val seconds: Int)
        : Condition(operator)

    data class NumberOfTimes(val operator: Operator, val count: Int) : Condition(operator)
    data class RandomInterval(val operator: Operator, val minMinutes: Int, val minSeconds: Int,
                              val maxMinutes: Int, val maxSeconds: Int) : Condition(operator)
}

enum class Operator {
    AND, OR
}
