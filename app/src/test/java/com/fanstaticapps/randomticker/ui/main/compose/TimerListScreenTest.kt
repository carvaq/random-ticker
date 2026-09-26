package com.fanstaticapps.randomticker.ui.main.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.fanstaticapps.randomticker.ui.main.TimerItemUiState
import com.fanstaticapps.randomticker.ui.main.TimersScreenUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.time.Duration.Companion.minutes

@RunWith(RobolectricTestRunner::class)
class TimerListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `shows NoTimersScreen when timer list is empty`() {
        var addClicked = false
        composeTestRule.setContent {
            MaterialTheme {
                TimerListScreen(
                    timerState = TimersScreenUiState.Success(emptyList()),
                    onStartTimerAction = {},
                    onStopTimerAction = {},
                    onTimerClick = {},
                    onAddTimerClick = { addClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Add Your First Timer").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Your First Timer").performClick()
        assertTrue(addClicked)
    }

    @Test
    fun `shows list of timers and handles timer click and action toggle`() {
        val timer =
            TimerItemUiState(
                id = 1L,
                name = "Morning Stretch",
                minInterval = 5.minutes,
                maxInterval = 10.minutes,
                autoRepeat = false,
                alarmSound = null,
                isRunning = false
            )
        var clickedTimerId: Long? = null

        composeTestRule.setContent {
            MaterialTheme {
                TimerListScreen(
                    timerState = TimersScreenUiState.Success(listOf(timer)),
                    onStartTimerAction = {},
                    onStopTimerAction = {},
                    onTimerClick = { clickedTimerId = it },
                    onAddTimerClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Morning Stretch").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morning Stretch").performClick()
        assertEquals(1L, clickedTimerId)
    }
}