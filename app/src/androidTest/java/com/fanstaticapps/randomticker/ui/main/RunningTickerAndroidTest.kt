package com.fanstaticapps.randomticker.ui.main


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TickerPreferences
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import javax.inject.Inject

@HiltAndroidTest
class RunningTickerAndroidTest {
    private val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val rule: RuleChain = RuleChain.outerRule(hiltRule)
        .around(activityScenarioRule<MainActivity>())

    @Inject
    lateinit var tickerPreferences: TickerPreferences

    @Before
    fun setUp() {
        hiltRule.inject()
        tickerPreferences.setTickerInterval(40000)
    }

    @Test
    fun testThatIfTickerIsRunningGoToKlaxon() {
        onView(withId(R.id.btnDismiss)).perform(scrollTo(), click())
    }
}
