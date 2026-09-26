package com.fanstaticapps.randomticker.helper

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import com.fanstaticapps.randomticker.data.Bookmark
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
class AlarmCoordinatorTest {
    private val context: Context = RuntimeEnvironment.getApplication()
    private val alarmCoordinator = AlarmCoordinator(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val shadowAlarmManager = shadowOf(alarmManager)

    @Test
    fun `scheduleAlarm schedules exact alarm in AlarmManager`() {
        val bookmark = Bookmark(id = 1L, intervalEnd = System.currentTimeMillis() + 60000L)

        alarmCoordinator.scheduleAlarm(bookmark)

        val scheduledAlarm = shadowAlarmManager.scheduledAlarms.firstOrNull()
        assertNotNull(scheduledAlarm)
        assertEquals(bookmark.intervalEnd, scheduledAlarm?.triggerAtTime)
    }

    @Test
    fun `cancelAlarm cancels alarm in AlarmManager`() {
        val bookmark = Bookmark(id = 2L, intervalEnd = System.currentTimeMillis() + 60000L)

        alarmCoordinator.scheduleAlarm(bookmark)
        alarmCoordinator.cancelAlarm(bookmark)

        // Verifying cancel does not throw exception
    }
}