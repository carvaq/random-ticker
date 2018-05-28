package com.cvv.fanstaticapps.randomticker.helper

/**
 * Created by carvaq
 * Date: 5/28/18
 * Project: random-ticker
 */
class IntegerUtil {
    companion object {
        fun getIntegerFromCharSequence(s: CharSequence): Int {
            return if (s.isBlank()) {
                0
            } else {
                s.toString().toInt()
            }
        }
    }
}