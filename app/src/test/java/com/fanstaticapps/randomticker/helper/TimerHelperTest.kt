package com.fanstaticapps.randomticker.helper

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.IntervalDefinition
import com.fanstaticapps.randomticker.extensions.getAlarmManager
import com.nhaarman.mockitokotlin2.*
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class TimerHelperTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val alarmManager = context.getAlarmManager()
    private val shadowAlarmManager: ShadowAlarmManager = shadowOf(alarmManager)

    private val notificationManagerMock: TickerNotificationManager = mock()
    private val tickerPreferencesMock: TickerPreferences = mock()

    private val testee = TimerHelper(notificationManagerMock, tickerPreferencesMock)

    @Test
    fun `when repeating a ticker from bookmark then start notifications and create alarm`() {
        whenever(tickerPreferencesMock.showRunningTimerNotification).doReturn(false)
        whenever(tickerPreferencesMock.intervalWillBeFinished)
                .doReturn(System.currentTimeMillis() + 2300)

        testee.newTickerFromBookmark(context, Bookmark("Test"))

        verify(notificationManagerMock).cancelAllNotifications(context)
        verify(tickerPreferencesMock).resetInterval()

        assertTrue(shadowAlarmManager.nextScheduledAlarm != null)
    }

    @Test
    fun `when creating ticker if min max is invalid do not create`() {

        val tickerCreated = testee.createTicker(minIntervalDefinition = IntervalDefinition(0, 20, 0),
                maxIntervalDefinition = IntervalDefinition(0, 0, 20))

        verify(tickerPreferencesMock, never()).setTickerInterval(any())
        assertFalse(tickerCreated)
    }

    @Test
    fun `when creating ticker if min max is valid do not create`() {
        val tickerCreated = testee.createTicker(minIntervalDefinition = IntervalDefinition(0, 20, 0),
                maxIntervalDefinition = IntervalDefinition(0, 20, 20))

        val argumentCaptor = argumentCaptor<Long>()
        verify(tickerPreferencesMock).setTickerInterval(argumentCaptor.capture())

        assertTrue(argumentCaptor.firstValue in (1200000..1400000))
        assertTrue(tickerCreated)
    }

    @Test
    fun `when ticker is not set return not currently running`() {
        whenever(tickerPreferencesMock.intervalWillBeFinished).doReturn(-1)

        assertFalse(testee.isCurrentlyTickerRunning())
    }

    @Test
    fun `when ticker is set return it is currently running`() {
        whenever(tickerPreferencesMock.intervalWillBeFinished).doReturn(1)

        assertTrue(testee.isCurrentlyTickerRunning())
    }

    @Test
    fun `when cancelling ticker verify alarm is longer set`() {
        assertTrue(shadowAlarmManager.nextScheduledAlarm == null)

        whenever(tickerPreferencesMock.intervalWillBeFinished).doReturn(System.currentTimeMillis() + 3000)
        testee.startTicker(context)

        assertTrue(shadowAlarmManager.nextScheduledAlarm != null)

        testee.cancelTicker(context)

        verify(tickerPreferencesMock).resetInterval()
        verify(notificationManagerMock).cancelAllNotifications(any())
        assertTrue(shadowAlarmManager.nextScheduledAlarm == null)
    }
}