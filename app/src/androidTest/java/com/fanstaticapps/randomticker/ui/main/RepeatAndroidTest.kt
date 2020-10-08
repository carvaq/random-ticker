package com.fanstaticapps.randomticker.ui.main


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.pickValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@HiltAndroidTest
class RepeatAndroidTest {

    @get:Rule
    val rule: RuleChain = RuleChain.outerRule(HiltAndroidRule(this))
            .around(activityScenarioRule<MainActivity>())


    @Test
    fun testWhenRepeatIsCheckedTheRepeatButtonIsNotDisplayed() {

        createTicker()
        onView(withId(R.id.cbAutoRepeat)).perform(click())
        onView(withId(R.id.btnStartTicker)).perform(click())

        repeat(7) {
            Thread.sleep(1000)
            onView(withId(R.id.btnRepeat)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun testWhenRepeatIsNotCheckedTheRepeatButtonIsDisplayed() {

        createTicker()
        onView(withId(R.id.btnStartTicker)).perform(click())

        repeat(7) {
            Thread.sleep(1000)
            onView(withId(R.id.btnRepeat)).check(matches(isDisplayed()))
        }
    }

    private fun createTicker() {
        onView(withId(R.id.etBookmarkName))
                .perform(replaceText("Alisteir"), closeSoftKeyboard())

        onView(withId(R.id.minSec)).perform(pickValue(4))
        onView(withId(R.id.maxSec)).perform(pickValue(7))

    }


}
