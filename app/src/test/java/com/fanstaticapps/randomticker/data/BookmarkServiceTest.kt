package com.fanstaticapps.randomticker.data

import com.fanstaticapps.randomticker.helper.AlarmCoordinator
import com.fanstaticapps.randomticker.helper.NotificationCoordinator
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkServiceTest {
    
    private val repository: BookmarkRepository = mockk(relaxed = true)
    private val notificationCoordinator: NotificationCoordinator = mockk(relaxed = true)
    private val alarmCoordinator: AlarmCoordinator = mockk(relaxed = true)
    private val testScope = TestScope()
    private val currentTimestamp = 1778751171000
    private val clock = mockk<Clock> {
        every { now() } returns Instant.fromEpochMilliseconds(currentTimestamp)
    }
    private val bookmarkService = BookmarkService(repository, notificationCoordinator, alarmCoordinator, clock, testScope)
    
    @After
    fun cleanup() {
        clearAllMocks()
    }
    
    @Test
    fun `getBookmarkById should return flow of bookmark`() = testScope.runTest {
        val bookmarkId = 1L
        val bookmark = Bookmark(id = bookmarkId)
        mockBookmarkInRepository(bookmark)
        
        val flow = bookmarkService.getBookmarkById(bookmarkId)
        
        assertEquals(bookmark, flow.first())
    }
    
    @Test
    fun `save should insert or update bookmark`() = testScope.runTest {
        val bookmark = Bookmark(id = 1)
        
        bookmarkService.save(bookmark).join()
        
        coVerify { repository.insertOrUpdateBookmark(bookmark) }
    }
    
    @Test
    fun `createNew should create and notify new bookmark`() = testScope.runTest {
        val newId = 2L
        coEvery { repository.insertOrUpdateBookmark(any()) } returns newId
        
        val result = bookmarkService.createNew()
        
        assertEquals(newId, result)
        
        coVerify { repository.insertOrUpdateBookmark(match { it.id == Bookmark.NOT_SET_VALUE }) }
        coVerify { notificationCoordinator.triggerNotificationChannelNotification(match { it.id == newId }) }
    }
    
    @Test
    fun `intervalEnded should cancel notifications and schedule alarm when autoRepeat is true`() =
        testScope.runTest {
            val bookmarkId = 1L
            val bookmark = Bookmark(id = bookmarkId, autoRepeat = true)
            mockBookmarkInRepository(bookmark)
            
            bookmarkService.updateWithIntervalEnded(bookmarkId).join()
            
            coVerifyOrder {
                notificationCoordinator.cancelAllNotifications(any())
                notificationCoordinator.showKlaxonNotification(any())
                notificationCoordinator.cancelAllNotifications(any())
                notificationCoordinator.showRunningNotification(any())
                alarmCoordinator.scheduleAlarm(any())
            }
            coVerify(exactly = 0) { alarmCoordinator.cancelAlarm(any()) }
        }
    
    @Test
    fun `intervalEnded should cancel notifications and not schedule alarm when autoRepeat is false`() =
        testScope.runTest {
            val bookmarkId = 2L
            val bookmark = Bookmark(id = bookmarkId, autoRepeat = false)
            mockBookmarkInRepository(bookmark)
            
            bookmarkService.updateWithIntervalEnded(bookmarkId).join()
            
            coVerify { notificationCoordinator.cancelAllNotifications(any()) }
            coVerify { notificationCoordinator.showKlaxonNotification(any()) }
            
            coVerify(exactly = 0) { alarmCoordinator.scheduleAlarm(any()) }
            coVerify(exactly = 0) { alarmCoordinator.cancelAlarm(any()) }
            coVerify(exactly = 0) { notificationCoordinator.showRunningNotification(any()) }
            
        }
    
    @Test
    fun `scheduleAlarm should delay if not manually triggered and autoRepeat is true`() =
        testScope.runTest {
            val bookmarkId = 3L
            val bookmark = Bookmark(id = bookmarkId, autoRepeat = true)
            mockBookmarkInRepository(bookmark)
            
            bookmarkService.scheduleAlarm(bookmarkId, isManuallyTriggered = false)
            coVerify(exactly = 0) { alarmCoordinator.scheduleAlarm(any()) }
            
            advanceTimeBy(bookmark.autoRepeatInterval + 20)
            
            coVerify(exactly = 1) { alarmCoordinator.scheduleAlarm(any()) }
            coVerify { notificationCoordinator.cancelAllNotifications(any()) }
            coVerify { notificationCoordinator.showRunningNotification(any()) }
        }
    
    @Test
    fun `scheduleAlarm should not delay if manually triggered`() = testScope.runTest {
        val bookmarkId = 4L
        val bookmark = spyk(Bookmark(id = bookmarkId, autoRepeat = true))
        
        mockBookmarkInRepository(bookmark)
        
        bookmarkService.scheduleAlarm(bookmarkId, isManuallyTriggered = true).join()
        
        coVerify { notificationCoordinator.cancelAllNotifications(any()) }
        coVerify { notificationCoordinator.showRunningNotification(any()) }
        coVerify { alarmCoordinator.scheduleAlarm(any()) }
        coVerify(exactly = 0) { bookmark.autoRepeatInterval }
    }
    
    @Test
    fun `scheduleAlarm should calculate random interval correctly`() = testScope.runTest {
        val bookmarkId = 100L
        val bookmark = Bookmark(
            id = bookmarkId,
            maximumMinutes = 0,
            minimumSeconds = 10,
            maximumSeconds = 20
        )
        mockBookmarkInRepository(bookmark)
        
        bookmarkService.scheduleAlarm(bookmarkId, isManuallyTriggered = true).join()
        
        coVerify {
            repository.insertOrUpdateBookmark(match {
                val interval = it.intervalEnd - currentTimestamp
                interval in 10_000..20_000
            })
        }
    }
    
    @Test
    fun `cancel should reset bookmark and cancel alarm`() = testScope.runTest {
        val bookmarkId = 7L
        val bookmark = Bookmark(id = bookmarkId, intervalEnd = 12345L)
        mockBookmarkInRepository(bookmark)
        
        bookmarkService.cancel(bookmarkId).join()
        
        coVerify { repository.insertOrUpdateBookmark(match { it.id == bookmarkId && it.intervalEnd == Bookmark.NOT_SET_VALUE }) }
        coVerify { notificationCoordinator.cancelAllNotifications(any()) }
        coVerify { alarmCoordinator.cancelAlarm(any()) }
    }
    
    @Test
    fun `delete should cancel timer and delete bookmark`() =
        testScope.runTest {
            val bookmarkId = 6L
            val bookmark = Bookmark(id = bookmarkId)
            mockBookmarkInRepository(bookmark)
            
            bookmarkService.delete(bookmarkId).join()
            
            
            coVerify { repository.insertOrUpdateBookmark(any()) }
            
            coVerify { notificationCoordinator.deleteChannelsForBookmark(any()) }
            
            coVerify { repository.deleteBookmark(any()) }
            
            coVerify { alarmCoordinator.cancelAlarm(any()) }
            
        }
    
    @Test
    fun `updateAllBookmarks should only update when changed`() = testScope.runTest {
        val bookmarks = listOf(Bookmark(id = 1), Bookmark(id = 2))
        coEvery { repository.getAllBookmarksOnce() } returns bookmarks
        
        bookmarkService.updateAllBookmarks { it }.join()
        coVerify(exactly = 0) { repository.bulkUpdate(any()) }
        
        bookmarkService.updateAllBookmarks { it.copy(name = "New Name") }.join()
        coVerify(exactly = 1) { repository.bulkUpdate(any()) }
    }
    
    private fun mockBookmarkInRepository(bookmark: Bookmark) {
        coEvery { repository.getBookmarkById(bookmark.id) } returns flowOf(bookmark)
        coEvery { repository.getBookmarkByIdOnce(bookmark.id) } returns bookmark
    }
    
}