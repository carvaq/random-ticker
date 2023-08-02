package com.fanstaticapps.randomticker.data

data class IntervalDefinition(val hours: Int, val minutes: Int, val seconds: Int) :
    Comparable<IntervalDefinition> {

    val millis: Long = (60 * 60 * hours + 60 * minutes + seconds) * 1000L

    operator fun plus(intervalDefinition: IntervalDefinition) = millis + intervalDefinition.millis

    operator fun minus(intervalDefinition: IntervalDefinition) = millis - intervalDefinition.millis
    override fun compareTo(other: IntervalDefinition) = millis.compareTo(other.millis)

}