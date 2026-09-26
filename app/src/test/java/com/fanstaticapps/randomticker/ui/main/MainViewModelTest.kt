package com.fanstaticapps.randomticker.ui.main

import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val bookmarkService: BookmarkService = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `timers flow emits TimersScreenUiState Success with mapped TimerItemUiState list`() =
        runTest {
            val bookmark1 =
                Bookmark(
                    id = 1,
                    name = "Timer 1",
                    minimumMinutes = 1,
                    maximumMinutes = 5,
                    autoRepeat = true,
                    soundUri = "uri1",
                    intervalEnd = 1000L
                )
            val bookmark2 =
                Bookmark(
                    id = 2,
                    name = "Timer 2",
                    minimumMinutes = 2,
                    maximumMinutes = 10,
                    autoRepeat = false,
                    soundUri = null,
                    intervalEnd = Bookmark.NOT_SET_VALUE
                )
            every { bookmarkService.fetchAllBookmarks() } returns flowOf(listOf(bookmark1, bookmark2))

            val viewModel = MainViewModel(bookmarkService)

            // Advance time so that runningTimers flow emits an initial tick
            advanceTimeBy(1001)

            val state = viewModel.timers.first()

            assertTrue(state is TimersScreenUiState.Success)
            val successState = state as TimersScreenUiState.Success
            assertEquals(2, successState.timers.size)

            val item1 = successState.timers[0]
            assertEquals(1L, item1.id)
            assertEquals("Timer 1", item1.name)
            assertEquals(1.minutes, item1.minInterval)
            assertEquals(5.minutes, item1.maxInterval)
            assertTrue(item1.autoRepeat)
            assertEquals("uri1", item1.alarmSound)
            assertTrue(item1.isRunning)
            assertEquals(1000L, item1.endTimeMillis)

            val item2 = successState.timers[1]
            assertEquals(2L, item2.id)
            assertEquals("Timer 2", item2.name)
            assertEquals(2.minutes, item2.minInterval)
            assertEquals(10.minutes, item2.maxInterval)
            assertEquals(false, item2.autoRepeat)
            assertEquals(null, item2.alarmSound)
            assertEquals(false, item2.isRunning)
            assertEquals(Bookmark.NOT_SET_VALUE, item2.endTimeMillis)
        }

    @Test
    fun `start calls scheduleAlarm on bookmarkService`() {
        val viewModel = MainViewModel(bookmarkService)
        viewModel.start(42L)

        verify { bookmarkService.scheduleAlarm(42L, true) }
    }

    @Test
    fun `cancelTimer calls cancel on bookmarkService`() {
        val viewModel = MainViewModel(bookmarkService)
        viewModel.cancelTimer(42L)

        verify { bookmarkService.cancel(42L) }
    }

    @Test
    fun `save calls save on bookmarkService with correct bookmark`() {
        val viewModel = MainViewModel(bookmarkService)
        val timerItem =
            TimerItemUiState(
                id = 5L,
                name = "Workout",
                minInterval = 30.seconds,
                maxInterval = 2.minutes,
                autoRepeat = true,
                alarmSound = "sound_uri"
            )

        viewModel.save(timerItem)

        verify {
            bookmarkService.save(
                match {
                    it.id == 5L &&
                        it.name == "Workout" &&
                        it.minimumSeconds == 30 &&
                        it.maximumMinutes == 2 &&
                        it.autoRepeat &&
                        it.soundUri == "sound_uri"
                }
            )
        }
    }

    @Test
    fun `delete calls delete on bookmarkService`() {
        val viewModel = MainViewModel(bookmarkService)
        viewModel.delete(100L)

        verify { bookmarkService.delete(100L) }
    }
}