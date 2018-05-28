package com.cvv.fanstaticapps.randomticker.activities

import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import com.cvv.fanstaticapps.randomticker.R
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

        onView(withId(R.id.minMin)).perform(typeText("0"), closeSoftKeyboard())
        onView(withId(R.id.minSec)).perform(typeText("2"), closeSoftKeyboard())

        onView(withId(R.id.maxMin)).perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.maxSec)).perform(typeText("4"), closeSoftKeyboard())


        val device = UiDevice.getInstance(getInstrumentation())

        device.setOrientationRight()

        onView(withId(R.id.minMin)).check(matches(withText("0")))
        onView(withId(R.id.minSec)).check(matches(withText("2")))
        onView(withId(R.id.maxMin)).check(matches(withText("1")))
        onView(withId(R.id.maxSec)).check(matches(withText("4")))

        device.setOrientationNatural()

        Thread.sleep(100)

        onView(withId(R.id.start)).perform(click())

        onView(withId(R.id.pulsator)).check(matches(isDisplayed()))
    }

}
