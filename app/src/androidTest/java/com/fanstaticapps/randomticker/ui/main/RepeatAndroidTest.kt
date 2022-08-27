package com.fanstaticapps.randomticker.ui.main


import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TickerFinishedIdlingResource
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.pickValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class RepeatAndroidTest {


    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var tickerPreferences: TickerPreferences

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
    }

    @Test
    fun testWhenRepeatIsCheckedTheRepeatButtonIsNotDisplayed(): Unit =
        launchActivity<MainActivity>().use {
            createTicker()

            onView(withId(R.id.cbAutoRepeat)).perform(scrollTo(), click())
            onView(withId(R.id.btnStartTicker)).perform(scrollTo(), click())

            registerIdlingResource()

            onView(withId(R.id.btnRepeat)).check(matches(not(isDisplayed())))
        }


    @Test
    fun testWhenRepeatIsNotCheckedTheRepeatButtonIsDisplayed(): Unit =
        launchActivity<MainActivity>().use {
            createTicker()

            onView(withId(R.id.btnStartTicker)).perform(scrollTo(), click())

            registerIdlingResource()

            onView(withId(R.id.btnRepeat)).check(matches(isDisplayed()))
        }

    private fun createTicker() {
        onView(withId(R.id.etBookmarkName))
            .perform(replaceText("Alisteir"), closeSoftKeyboard())

        onView(withId(R.id.minSec)).perform(pickValue(2))
        onView(withId(R.id.maxSec)).perform(pickValue(4))

    }

    private fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(TickerFinishedIdlingResource(tickerPreferences))
    }

}