package com.fanstaticapps.randomticker.data

import com.fanstaticapps.randomticker.helper.AlarmCoordinator
import com.fanstaticapps.randomticker.helper.NotificationCoordinator
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkServiceTest {

    private val repository: BookmarkRepository = mockk(relaxed = true)
    private val notificationCoordinator: NotificationCoordinator = mockk(relaxed = true)
    private val alarmCoordinator: AlarmCoordinator = mockk(relaxed = true)

    @After
    fun cleanup() {
        clearAllMocks()
    }

    @Test
    fun `getBookmarkById should return flow of bookmark`() = runTest {
        val bookmarkId = 1L
        val bookmark = Bookmark(id = bookmarkId)
        mockBookmarkInRepository(bookmarkId, bookmark)

        val flow = getBookmarkService().getBookmarkById(bookmarkId)
        runCurrent()

        assertEquals(bookmark, flow.first())
    }

    @Test
    fun `save should insert or update bookmark`() = runTest {
        val bookmark = Bookmark(id = 1)

        getBookmarkService().save(bookmark)
        runCurrent()

        coVerify { repository.insertOrUpdateBookmark(bookmark) }
    }

    @Test
    fun `createNew should create and notify new bookmark`() = runTest {
        val newId = 2L
        coEvery { repository.insertOrUpdateBookmark(any()) } returns newId

        val result = getBookmarkService().createNew()
        runCurrent()

        assertEquals(newId, result)
        coVerify {
            repository.insertOrUpdateBookmark(match { it.id == 0L })
            notificationCoordinator.triggerNotificationChannelNotification(match { it.id == newId })
        }
    }

    @Test
    fun `intervalEnded should cancel notifications and schedule alarm when autoRepeat is true`() =
        runTest {
            val bookmarkId = 1L
            val bookmark = Bookmark(id = bookmarkId, autoRepeat = true)
            mockBookmarkInRepository(bookmarkId, bookmark)

            getBookmarkService().intervalEnded(bookmarkId).join()
            runCurrent()

            coVerifyOrder {
                notificationCoordinator.cancelAllNotifications(any())
                notificationCoordinator.showKlaxonNotification(any())
                notificationCoordinator.cancelAllNotifications(any())
                notificationCoordinator.showRunningNotification(any())
                alarmCoordinator.scheduleAlarm(any())
            }
            coVerify(exactly = 0) {
                alarmCoordinator.cancelAlarm(any())
            }
        }

    @Test
    fun `intervalEnded should cancel notifications and not schedule alarm when autoRepeat is false`() =
        runTest {
            val bookmarkId = 2L
            val bookmark = Bookmark(id = bookmarkId, autoRepeat = false)
            mockBookmarkInRepository(bookmarkId, bookmark)

            getBookmarkService().intervalEnded(bookmarkId).join()
            runCurrent()

            coVerify {
                notificationCoordinator.cancelAllNotifications(any())
                notificationCoordinator.showKlaxonNotification(any())
            }
            coVerify(exactly = 0) {
                alarmCoordinator.scheduleAlarm(any())
                alarmCoordinator.cancelAlarm(any())
                notificationCoordinator.showRunningNotification(any())
            }
        }

    @Test
    fun `scheduleAlarm should delay if not manually triggered and autoRepeat is true`() =
        runTest {
            val bookmarkId = 3L
            val bookmark = spyBookmark(
                bookmarkId = bookmarkId,
                bookmarkAutoRepeat = true
            )
            mockBookmarkInRepository(bookmarkId, bookmark)

            getBookmarkService().scheduleAlarm(bookmarkId, isManuallyTriggered = false).join()

            coVerify {
                notificationCoordinator.cancelAllNotifications(any())
                notificationCoordinator.showRunningNotification(any())
                alarmCoordinator.scheduleAlarm(any())
            }
            coVerify(exactly = 1) { bookmark.autoRepeatInterval }
        }

    @Test
    fun `scheduleAlarm should not delay if manually triggered`() = runTest {
        val bookmarkId = 4L
        val bookmark = spyBookmark(bookmarkId = bookmarkId, bookmarkAutoRepeat = true)
        mockBookmarkInRepository(bookmarkId, bookmark)

        getBookmarkService().scheduleAlarm(bookmarkId, isManuallyTriggered = true).join()
        runCurrent()

        coVerify {
            notificationCoordinator.cancelAllNotifications(any())
            notificationCoordinator.showRunningNotification(any())
            alarmCoordinator.scheduleAlarm(any())
        }
        coVerify(exactly = 0) { bookmark.autoRepeatInterval }
    }

    @Test
    fun `scheduleAlarm should not delay if autoRepeat is false`() = runTest {
        val bookmarkId = 5L
        val bookmark = spyBookmark(bookmarkId = bookmarkId, bookmarkAutoRepeat = false)
        mockBookmarkInRepository(bookmarkId, bookmark)

        getBookmarkService().scheduleAlarm(bookmarkId, isManuallyTriggered = false).join()
        runCurrent()

        coVerify {
            notificationCoordinator.cancelAllNotifications(any())
            notificationCoordinator.showRunningNotification(any())
            alarmCoordinator.scheduleAlarm(any())
        }
        coVerify(exactly = 0) { bookmark.autoRepeatInterval }
    }

    private fun TestScope.getBookmarkService() =
        BookmarkService(repository, notificationCoordinator, alarmCoordinator, this)

    @Test
    fun `delete should cancel ticker, delete bookmark, and delete notification channels`() =
        runTest {
            val bookmarkId = 6L
            val bookmark = spyBookmark(bookmarkId = bookmarkId)
            mockBookmarkInRepository(bookmarkId, bookmark)
            coEvery { repository.insertOrUpdateBookmark(any()) } returns bookmarkId

            getBookmarkService().delete(bookmark).join()

            coVerify {
                repository.insertOrUpdateBookmark(any())
                notificationCoordinator.deleteChannelsForBookmark(any())
                repository.deleteBookmark(any())
                alarmCoordinator.cancelAlarm(any())
            }
            coVerify(exactly = 0) {
                notificationCoordinator.showRunningNotification(any())
                alarmCoordinator.scheduleAlarm(any())
                bookmark.autoRepeatInterval
            }
        }

    private fun mockBookmarkInRepository(bookmarkId: Long, bookmark: Bookmark) {
        coEvery { repository.getBookmarkById(bookmarkId) } returns flowOf(bookmark)
    }

    private fun spyBookmark(
        bookmarkId: Long,
        bookmarkAutoRepeat: Boolean = true
    ) = spyk(Bookmark(bookmarkId, autoRepeat = bookmarkAutoRepeat))

}