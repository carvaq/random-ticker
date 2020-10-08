package com.fanstaticapps.randomticker.ui.main


import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.pickValue
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class BookmarksAndroidTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun testBookmarkSaving() {
        activityScenarioRule.scenario

        onView(withId(R.id.etBookmarkName))
                .perform(replaceText("TestLife"), closeSoftKeyboard())

        onView(withId(R.id.minSec)).perform(pickValue(5))
        onView(withId(R.id.maxHours)).perform(pickValue(2))
        onView(withId(R.id.maxHours)).perform(pickValue(0))
        onView(withId(R.id.maxSec)).perform(pickValue(10))

        onView(withId(R.id.btnStartTicker)).perform(click())

        onView(withId(R.id.btnRepeat)).check(matches(not(isDisplayed())))

        onView(withId(R.id.btnDismiss)).perform(click())

        onView(withId(R.id.etBookmarkName)).check(matches(withText("TestLife")))
    }


    @Test
    fun testBookmarkSelection() {
        activityScenarioRule.scenario

        createTicker("Alisteir", 5, 10)

        onView(withId(R.id.btnSelectBookmark)).perform(click())
        onView(withId(R.id.rvBookmarks))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        createTicker("Lucinda", 20, 21)

        onView(withId(R.id.etBookmarkName)).check(matches(withText("Lucinda")))

        onView(withId(R.id.btnSelectBookmark)).perform(click())
        onView(withId(R.id.rvBookmarks))
                .check(matches(hasDescendant(withText("Alisteir"))))
        onView(withId(R.id.rvBookmarks))
                .check(matches(hasDescendant(withText("Lucinda"))))
    }

    private fun createTicker(name: String, minSec: Int, maxSec: Int) {
        onView(withId(R.id.etBookmarkName))
                .perform(replaceText(name), closeSoftKeyboard())

        onView(withId(R.id.minSec)).perform(pickValue(minSec))
        onView(withId(R.id.maxSec)).perform(pickValue(maxSec))

        onView(withId(R.id.btnStartTicker)).perform(click())
        onView(withId(R.id.btnDismiss)).perform(click())
    }


}
