package com.fanstaticapps.randomticker.ui.klaxon

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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class KlaxonViewModelTest {
    private val service: BookmarkService = mockk(relaxed = true)
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
    fun `getCurrentBookmark returns bookmark flow from service`() =
        runTest {
            val bookmark = Bookmark(id = 10L, name = "Test Klaxon")
            every { service.getBookmarkById(10L) } returns flowOf(bookmark)

            val viewModel = KlaxonViewModel(service)
            val result = viewModel.getCurrentBookmark(10L).first()

            assertEquals(bookmark, result)
        }

    @Test
    fun `cancelTimer calls cancel on service`() {
        val viewModel = KlaxonViewModel(service)
        val bookmark = Bookmark(id = 15L)

        viewModel.cancelTimer(bookmark)

        verify { service.cancel(15L) }
    }

    @Test
    fun `scheduleTicker calls scheduleAlarm on service`() {
        val viewModel = KlaxonViewModel(service)
        val bookmark = Bookmark(id = 20L)

        viewModel.scheduleTicker(bookmark)

        verify { service.scheduleAlarm(20L, true) }
    }

    @Test
    fun `needsRestartDueToAutoRepeat emits bookmark when autoRepeat is true`() =
        runTest {
            val bookmark = Bookmark(id = 5L, autoRepeat = true, autoRepeatInterval = 1000L)
            every { service.getBookmarkById(5L) } returns flowOf(bookmark)

            val viewModel = KlaxonViewModel(service)
            val liveData = viewModel.needsRestartDueToAutoRepeat(5L)

            liveData.observeForever {}
            advanceTimeBy(1001L)

            assertEquals(bookmark, liveData.value)
        }
}