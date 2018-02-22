package com.cvv.fanstaticapps.randomticker.activities;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.helper.PrefUserSettings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ExistingTimerFlowTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getInstrumentation()
                        .getTargetContext()
                        .getApplicationContext());
        PrefUserSettings prefUserSettings = new PrefUserSettings(sharedPreferences);
        prefUserSettings.setCurrentlyTickerRunning(true);
        prefUserSettings.setIntervalFinished(System.currentTimeMillis() + 10000);
        activityTestRule.launchActivity(null);
    }

    @Test
    public void testRunningTimerFlow() throws Exception {
        onView(withId(R.id.pulsator)).check(matches(isDisplayed()));
        onView(withId(R.id.waiting_icon)).check(matches(not(isDisplayed())));
    }

}
