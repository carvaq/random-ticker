package com.fanstaticapps.randomticker.ui.main.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.fanstaticapps.randomticker.ui.main.TimerItemUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.time.Duration.Companion.minutes

@RunWith(RobolectricTestRunner::class)
class NewEditTimerScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `new timer screen displays auto repeat and buttons`() {
        var cancelClicked = false
        composeTestRule.setContent {
            MaterialTheme {
                NewEditTimerScreen(
                    timerDetails = null,
                    onSave = {},
                    onCancel = { cancelClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Timer will automatically restart when finished.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").performClick()
        assertTrue(cancelClicked)
    }

    @Test
    fun `edit existing timer screen pre-fills fields and saves on click`() {
        val timer =
            TimerItemUiState(
                id = 10L,
                name = "Meditation",
                minInterval = 2.minutes,
                maxInterval = 10.minutes,
                autoRepeat = true,
                alarmSound = null
            )
        var savedTimer: TimerItemUiState? = null

        composeTestRule.setContent {
            MaterialTheme {
                NewEditTimerScreen(
                    timerDetails = timer,
                    onSave = { savedTimer = it },
                    onCancel = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Meditation").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").performClick()

        assertEquals(10L, savedTimer?.id)
        assertEquals("Meditation", savedTimer?.name)
        assertTrue(savedTimer?.autoRepeat == true)
    }
}