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
                    id = id,
                    name = name,
                    minimumHours = minHours.toInt(),
                    minimumMinutes = minMinutes,
                    minimumSeconds = minSeconds,
                    maximumHours = maxHours.toInt(),
                    maximumMinutes = maxMinutes,
                    maximumSeconds = maxSeconds,
                    autoRepeat = autoRepeat,
                    soundUri = alarmSound
                )
            }
        }
    }
}
