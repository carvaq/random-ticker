package com.fanstaticapps.randomticker.ui.main

import com.fanstaticapps.randomticker.data.Bookmark
import kotlin.time.Duration

data class TimerItemUiState(
    val id: Long,
    val name: String,
    val minInterval: Duration,
    val maxInterval: Duration,
    val autoRepeat: Boolean,
    val alarmSound: String?,
    val isRunning: Boolean = false,
    val endTimeMillis: Long = 0
) {
    fun formattedRemainingTime(currentTimeMillis: Long = System.currentTimeMillis()): String {
        if (!isRunning || (endTimeMillis <= currentTimeMillis)) return "00s"
        val remainingMillis = endTimeMillis - currentTimeMillis
        val secondsTotal = remainingMillis / 1000
        val hours = secondsTotal / 3600
        val minutes = (secondsTotal % 3600) / 60
        val seconds = secondsTotal % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }

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
sealed interface TimersScreenUiState {
    object Loading : TimersScreenUiState
    data class Success(val timers: List<TimerItemUiState>) : TimersScreenUiState
}