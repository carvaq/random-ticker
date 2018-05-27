package com.cvv.fanstaticapps.randomticker.activities


import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.cvv.fanstaticapps.randomticker.R
import com.cvv.fanstaticapps.randomticker.helper.TickerData
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class ExistingTimerFlowTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setUp() {
        val prefUserSettings = TickerData(InstrumentationRegistry.getInstrumentation().targetContext)
        prefUserSettings.currentlyTickerRunning = true
        prefUserSettings.intervalFinished = System.currentTimeMillis() + 10000
        activityTestRule.launchActivity(null)
    }

    @Test
    fun testRunningTimerFlow() {
        onView(withId(R.id.pulsator)).check(matches(isDisplayed()))
        onView(withId(R.id.waitingIcon)).check(matches(not<View>(isDisplayed())))
    }

}
