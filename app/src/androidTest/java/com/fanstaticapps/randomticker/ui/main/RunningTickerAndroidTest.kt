package com.fanstaticapps.randomticker.ui.main


import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TickerPreferences
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class RunningTickerAndroidTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var tickerPreferences: TickerPreferences

    @Before
    fun setUp() {
        hiltRule.inject()
        tickerPreferences.setTickerInterval(40000)
    }

    @Test
    fun testThatIfTickerIsRunningGoToKlaxon() {
        launchActivity<MainActivity>().use {
            onView(withId(R.id.btnDismiss)).perform(click())
        }
    }
}