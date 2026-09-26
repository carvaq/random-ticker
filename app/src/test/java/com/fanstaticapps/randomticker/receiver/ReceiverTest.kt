package com.fanstaticapps.randomticker.receiver

import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.EXTRA_BOOKMARK_ID
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReceiverTest {
    private val context: Context = org.robolectric.RuntimeEnvironment.getApplication()
    private val bookmarkService: BookmarkService = mockk(relaxed = true)

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            modules(
                module {
                    single { bookmarkService }
                }
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `AlarmEndedReceiver onReceive with valid bookmarkId updates bookmark with interval ended`() {
        val receiver = AlarmEndedReceiver()
        val intent =
            Intent(AlarmEndedReceiver.ACTION).apply {
                putExtra(EXTRA_BOOKMARK_ID, 42L)
            }

        receiver.onReceive(context, intent)

        verify { bookmarkService.updateWithIntervalEnded(42L) }
    }

    @Test
    fun `AlarmEndedReceiver onReceive without bookmarkId does nothing`() {
        val receiver = AlarmEndedReceiver()
        val intent = Intent(AlarmEndedReceiver.ACTION)

        receiver.onReceive(context, intent)

        verify(exactly = 0) { bookmarkService.updateWithIntervalEnded(any()) }
    }

    @Test
    fun `CreateAlarmReceiver onReceive with valid bookmarkId schedules alarm`() {
        val receiver = CreateAlarmReceiver()
        val intent =
            Intent().apply {
                putExtra(EXTRA_BOOKMARK_ID, 88L)
            }

        receiver.onReceive(context, intent)

        verify { bookmarkService.scheduleAlarm(88L, false) }
    }

    @Test
    fun `CreateAlarmReceiver onReceive without bookmarkId does nothing`() {
        val receiver = CreateAlarmReceiver()
        val intent = Intent()

        receiver.onReceive(context, intent)

        verify(exactly = 0) { bookmarkService.scheduleAlarm(any(), any()) }
    }
}