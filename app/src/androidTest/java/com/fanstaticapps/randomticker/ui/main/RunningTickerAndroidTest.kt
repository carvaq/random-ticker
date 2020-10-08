package com.fanstaticapps.randomticker.ui.main


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TickerPreferences
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@HiltAndroidTest
@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class RunningTickerAndroidTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var tickerPreferences: TickerPreferences

    @Before
    fun setUp() {
        hiltRule.inject()
        tickerPreferences.setTickerInterval(40000)
    }

    @Test
    fun testThatIfTickerIsRunningGoToKlaxon() {
        activityScenarioRule.scenario

        onView(withId(R.id.btnDismiss)).perform(click())
    }
}
