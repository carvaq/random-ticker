package com.fanstaticapps.randomticker.ui.main

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class TimerItemUiStateTest {
    @Test
    fun `toBookmark converts TimerItemUiState to Bookmark correctly`() {
        val state =
            TimerItemUiState(
                id = 7L,
                name = "Test State",
                minInterval = 1.hours + 15.minutes + 30.seconds,
                maxInterval = 2.hours + 45.minutes + 50.seconds,
                autoRepeat = true,
                alarmSound = "alarm_sound_uri",
                isRunning = true,
                endTimeMillis = 123456789L
            )

        val bookmark = state.toBookmark()

        assertEquals(7L, bookmark.id)
        assertEquals("Test State", bookmark.name)
        assertEquals(1, bookmark.minimumHours)
        assertEquals(15, bookmark.minimumMinutes)
        assertEquals(30, bookmark.minimumSeconds)
        assertEquals(2, bookmark.maximumHours)
        assertEquals(45, bookmark.maximumMinutes)
        assertEquals(50, bookmark.maximumSeconds)
        assertEquals(true, bookmark.autoRepeat)
        assertEquals("alarm_sound_uri", bookmark.soundUri)
    }
}