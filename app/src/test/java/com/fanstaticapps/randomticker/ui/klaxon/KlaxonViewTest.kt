package com.fanstaticapps.randomticker.ui.klaxon

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class KlaxonViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val bookmarkService: BookmarkService = mockk(relaxed = true)

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            modules(
                module {
                    single { bookmarkService }
                    viewModel { KlaxonViewModel(get()) }
                }
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `KlaxonView shows name and repeat button when autoRepeat is false`() {
        val bookmark = Bookmark(id = 5L, name = "Klaxon Ticker", autoRepeat = false)
        var mainActivityOpened = false

        composeTestRule.setContent {
            MaterialTheme {
                KlaxonView(bookmark = bookmark) {
                    mainActivityOpened = true
                }
            }
        }

        composeTestRule.onNodeWithText("Klaxon Ticker").assertIsDisplayed()
        composeTestRule.onNodeWithText("Repeat").assertIsDisplayed().performClick()

        verify { bookmarkService.scheduleAlarm(5L, true) }
        assertTrue(mainActivityOpened)
    }

    @Test
    fun `KlaxonView stop button cancels timer`() {
        val bookmark = Bookmark(id = 8L, name = "Klaxon Ticker Stop", autoRepeat = true)
        var mainActivityOpened = false

        composeTestRule.setContent {
            MaterialTheme {
                KlaxonView(bookmark = bookmark) {
                    mainActivityOpened = true
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Stop Timer").assertIsDisplayed().performClick()

        verify { bookmarkService.cancel(8L) }
        assertTrue(mainActivityOpened)
    }
}