package com.fanstaticapps.randomticker.data

data class Boundary(val hours: Int, val minutes: Int, val seconds: Int) :
    Comparable<Boundary> {

    val millis: Long = (60 * 60 * hours + 60 * minutes + seconds) * 1000L

    operator fun plus(boundary: Boundary) = millis + boundary.millis
    operator fun minus(boundary: Boundary) = millis - boundary.millis
    override fun compareTo(other: Boundary) = millis.compareTo(other.millis)
}
