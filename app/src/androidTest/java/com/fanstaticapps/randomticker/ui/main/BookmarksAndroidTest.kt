package com.fanstaticapps.randomticker.ui.main


import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
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
class BookmarksAndroidTest {

    @get:Rule
    val rule: RuleChain = RuleChain.outerRule(HiltAndroidRule(this))
        .around(activityScenarioRule<MainActivity>())

    @Test
    fun testBookmarkSaving() {

        onView(withId(R.id.etBookmarkName))
            .perform(replaceText("TestLife"), closeSoftKeyboard())

        onView(withId(R.id.minSec)).perform(pickValue(5))
        onView(withId(R.id.maxHours)).perform(pickValue(2))
        onView(withId(R.id.maxHours)).perform(pickValue(0))
        onView(withId(R.id.maxSec)).perform(pickValue(10))

        onView(withId(R.id.btnStartTicker)).perform(scrollTo(), click())

        onView(withId(R.id.btnRepeat)).check(matches(not(isDisplayed())))

        onView(withId(R.id.btnDismiss)).perform(click())

        onView(withId(R.id.etBookmarkName)).check(matches(withText("TestLife")))
    }


    @Test
    fun testBookmarkSelection() {
        createTicker("Alisteir", 5, 10)

        onView(withId(R.id.btnSelectBookmark)).perform(scrollTo(), click())
        onView(withId(R.id.rvBookmarks))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        createTicker("Lucinda", 20, 21)

        onView(withId(R.id.etBookmarkName)).check(matches(withText("Lucinda")))

        onView(withId(R.id.btnSelectBookmark)).perform(scrollTo(), click())
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

        onView(withId(R.id.btnStartTicker)).perform(scrollTo(), click())
        onView(withId(R.id.btnDismiss)).perform(click())
    }


}