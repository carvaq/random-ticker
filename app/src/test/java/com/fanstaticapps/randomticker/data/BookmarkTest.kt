package com.fanstaticapps.randomticker.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
class BookmarkTest {
    @Test
    fun `default values are correctly set`() {
        val bookmark = Bookmark()
        assertEquals(Bookmark.NOT_SET_VALUE, bookmark.id)
        assertEquals("Random Ticker", bookmark.name)
        assertEquals(0, bookmark.minimumHours)
        assertEquals(0, bookmark.minimumMinutes)
        assertEquals(0, bookmark.minimumSeconds)
        assertEquals(0, bookmark.maximumHours)
        assertEquals(5, bookmark.maximumMinutes)
        assertEquals(0, bookmark.maximumSeconds)
        assertFalse(bookmark.autoRepeat)
        assertEquals(Bookmark.DEFAULT_AUTO_REPEAT_INTERVAL, bookmark.autoRepeatInterval)
        assertEquals(Bookmark.NOT_SET_VALUE, bookmark.intervalEnd)
        assertEquals(null, bookmark.soundUri)
    }

    @Test
    fun `constructor with name sets name and default values`() {
        val bookmark = Bookmark("Custom Name")
        assertEquals("Custom Name", bookmark.name)
        assertEquals(Bookmark.NOT_SET_VALUE, bookmark.id)
        assertEquals(5.minutes, bookmark.max)
        assertEquals(0.seconds, bookmark.min)
    }

    @Test
    fun `reset clears intervalEnd`() {
        val bookmark = Bookmark(id = 1L, intervalEnd = 99999L)
        val resetBookmark = bookmark.reset()
        assertEquals(Bookmark.NOT_SET_VALUE, resetBookmark.intervalEnd)
        assertEquals(1L, resetBookmark.id)
    }

    @Test
    fun `min and max duration calculations are accurate`() {
        val bookmark =
            Bookmark(
                minimumHours = 1,
                minimumMinutes = 30,
                minimumSeconds = 45,
                maximumHours = 2,
                maximumMinutes = 15,
                maximumSeconds = 10
            )
        assertEquals(1.hours + 30.minutes + 45.seconds, bookmark.min)
        assertEquals(2.hours + 15.minutes + 10.seconds, bookmark.max)
    }

    @Test
    fun `notificationChannelId without soundUri returns default channel ID`() {
        val bookmark = Bookmark(soundUri = null)
        assertEquals("KLAXON-default", bookmark.notificationChannelId)
    }

    @Test
    fun `notificationChannelId with soundUri includes last path segment`() {
        val bookmark = Bookmark(soundUri = "content://settings/system/notification_sound")
        assertEquals("KLAXON-notification_sound", bookmark.notificationChannelId)
    }

    @Test
    fun `notification IDs are derived correctly from id`() {
        val bookmark = Bookmark(id = 42L)
        assertEquals(Int.MAX_VALUE - 42, bookmark.klaxonNotificationId)
        assertEquals(42, bookmark.runningNotificationId)
    }

    @Test
    fun `request codes are generated consistently`() {
        val bookmark = Bookmark(id = 10L)
        assertNotNull(bookmark.openAppRequestCode)
        assertNotNull(bookmark.cancelActionRequestCode)
        assertNotNull(bookmark.repeatReceiverRequestCode)
        assertNotNull(bookmark.klaxonActivityRequestCode)
    }
}