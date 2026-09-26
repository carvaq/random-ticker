package com.fanstaticapps.randomticker.helper

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.fanstaticapps.randomticker.data.Bookmark
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class NotificationCoordinatorTest {
    private val context: Context = RuntimeEnvironment.getApplication()
    private val coordinator = NotificationCoordinator(context)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val shadowNotificationManager = shadowOf(notificationManager)

    private val bookmark =
        Bookmark(
            id = 10L,
            name = "Test Notification Ticker",
            soundUri = null,
            autoRepeat = false
        )

    @Before
    fun setUp() {
        shadowOf(context as Application).grantPermissions(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    @Test
    fun `triggerNotificationChannelNotification creates notification channel`() {
        coordinator.triggerNotificationChannelNotification(bookmark)

        val channel = notificationManager.getNotificationChannel(bookmark.notificationChannelId)
        assertNotNull(channel)
    }

    @Test
    fun `showRunningNotification creates channel and posts notification`() {
        coordinator.showRunningNotification(bookmark)

        val channel = notificationManager.getNotificationChannel(bookmark.notificationChannelId)
        assertNotNull(channel)

        val notifications = shadowNotificationManager.allNotifications
        assertEquals(1, notifications.size)
    }

    @Test
    fun `showKlaxonNotification posts notification for non-repeating bookmark`() {
        coordinator.showKlaxonNotification(bookmark)

        val notifications = shadowNotificationManager.allNotifications
        assertEquals(1, notifications.size)
    }

    @Test
    fun `showKlaxonNotification posts notification for auto-repeating bookmark`() {
        val autoRepeatBookmark = bookmark.copy(autoRepeat = true)
        coordinator.showKlaxonNotification(autoRepeatBookmark)

        val notifications = shadowNotificationManager.allNotifications
        assertEquals(1, notifications.size)
    }

    @Test
    fun `cancelAllNotifications removes running and klaxon notifications`() {
        coordinator.showRunningNotification(bookmark)
        coordinator.showKlaxonNotification(bookmark)

        coordinator.cancelAllNotifications(bookmark)

        val activeNotifications = shadowNotificationManager.activeNotifications
        assertEquals(0, activeNotifications.size)
    }

    @Test
    fun `deleteChannelsForBookmark deletes notification channel`() {
        coordinator.triggerNotificationChannelNotification(bookmark)
        assertNotNull(notificationManager.getNotificationChannel(bookmark.notificationChannelId))

        coordinator.deleteChannelsForBookmark(bookmark)

        assertNull(notificationManager.getNotificationChannel(bookmark.notificationChannelId))
    }
}