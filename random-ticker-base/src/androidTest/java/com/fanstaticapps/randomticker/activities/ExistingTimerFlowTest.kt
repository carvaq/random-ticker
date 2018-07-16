package com.fanstaticapps.randomticker.activities


import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import android.view.View
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Preferences
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
        val prefUserSettings = Preferences(InstrumentationRegistry.getInstrumentation().targetContext)
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
