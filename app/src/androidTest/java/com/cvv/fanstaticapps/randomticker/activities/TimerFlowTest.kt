package com.cvv.fanstaticapps.randomticker.activities


import android.preference.PreferenceManager
import android.view.View
import android.widget.SeekBar
import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import com.cvv.fanstaticapps.randomticker.R
import com.cvv.fanstaticapps.randomticker.SetProgressAction.Companion.setProgress
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class TimerFlowTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        PreferenceManager.getDefaultSharedPreferences(activityTestRule.activity.applicationContext)
                .edit().clear().commit()
    }

    @Test
    @Throws(Exception::class)
    fun testTimerCreationFlow() {

        onView(withId(R.id.minMin)).perform(setProgress(0))
        onView(withId(R.id.minSec)).perform(setProgress(2))

        onView(withId(R.id.maxMin)).perform(setProgress(1))
        onView(withId(R.id.maxSec)).perform(setProgress(4))


        val device = UiDevice.getInstance(getInstrumentation())

        onView(withText(R.string.minimum_time)).check(matches(isDisplayed()))
        onView(withText(R.string.maximum_time)).check(matches(isDisplayed()))

        device.setOrientationRight()

        onView(withText(R.string.minimum_time)).check(matches(isDisplayed()))
        onView(withText(R.string.maximum_time)).check(matches(isDisplayed()))

        onView(withId(R.id.minMin)).check(matches(withProgress(0)))
        onView(withId(R.id.minSec)).check(matches(withProgress(2)))
        onView(withId(R.id.maxMin)).check(matches(withProgress(1)))
        onView(withId(R.id.maxSec)).check(matches(withProgress(4)))

        device.setOrientationNatural()

        Thread.sleep(100)

        onView(withId(R.id.start)).perform(click())

        onView(withId(R.id.pulsator)).check(matches(isDisplayed()))
    }

    private fun withProgress(expectedProgress: Int): Matcher<in View> {
        return object : BoundedMatcher<View, SeekBar>(SeekBar::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("with progress: ").appendValue(expectedProgress)
            }

            override fun matchesSafely(item: SeekBar): Boolean {
                return item.progress == expectedProgress
            }
        }
    }

}
