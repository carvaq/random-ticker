package com.fanstaticapps.randomticker.ui.main

import com.fanstaticapps.randomticker.data.Bookmark
import kotlin.time.Duration

data class TimerItemUiState(
    val id: Long,
    val name: String,
    val minInterval: Duration,
    val maxInterval: Duration,
    val timerEnd: Long,
    val autoRepeat: Boolean,
    val alarmSound: String?
) {

    fun isRunning() = timerEnd > System.currentTimeMillis()

    fun toBookmark(): Bookmark {
        return minInterval.toComponents { minHours, minMinutes, minSeconds, _ ->
            maxInterval.toComponents { maxHours, maxMinutes, maxSeconds, _ ->
                Bookmark(
                    id,
                    name,
                    minHours.toInt(),
                    minMinutes,
                    minSeconds,
                    maxHours.toInt(),
                    maxMinutes,
                    maxSeconds,
                    autoRepeat = autoRepeat,
                    soundUri = alarmSound
                )
            }
        }
    }
}
