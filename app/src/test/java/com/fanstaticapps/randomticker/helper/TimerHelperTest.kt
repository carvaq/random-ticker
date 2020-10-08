package com.fanstaticapps.randomticker.helper

import com.fanstaticapps.randomticker.TickerPreferences
import com.nhaarman.mockitokotlin2.mock
import junit.framework.TestCase
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class TimerHelperTest : TestCase() {
    private val notificationManagerMock: TickerNotificationManager = mock()
    private val tickerPreferencesMock: TickerPreferences = mock()

    private val testee = TimerHelper(notificationManagerMock, tickerPreferencesMock)


}