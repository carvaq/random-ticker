package com.cvv.fanstaticapps.randomticker.activities;


import android.preference.PreferenceManager;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.SeekBar;

import com.cvv.fanstaticapps.randomticker.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.cvv.fanstaticapps.randomticker.SetProgressAction.setProgress;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TimerFlowTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        PreferenceManager.getDefaultSharedPreferences(activityTestRule.getActivity().getApplicationContext())
                .edit().clear().commit();
    }

    @Test
    public void testTimerCreationFlow() throws Exception {

        onView(withId(R.id.min_min)).perform(setProgress(0));
        onView(withId(R.id.min_sec)).perform(setProgress(2));

        onView(withId(R.id.max_min)).perform(setProgress(1));
        onView(withId(R.id.max_sec)).perform(setProgress(4));


        UiDevice device = UiDevice.getInstance(getInstrumentation());

        onView(withText(R.string.minimum_time)).check(matches(isDisplayed()));
        onView(withText(R.string.maximum_time)).check(matches(isDisplayed()));

        device.setOrientationRight();

        onView(withText(R.string.minimum_time)).check(matches(isDisplayed()));
        onView(withText(R.string.maximum_time)).check(matches(isDisplayed()));

        onView(withId(R.id.min_min)).check(matches(withProgress(0)));
        onView(withId(R.id.min_sec)).check(matches(withProgress(2)));
        onView(withId(R.id.max_min)).check(matches(withProgress(1)));
        onView(withId(R.id.max_sec)).check(matches(withProgress(4)));

        device.setOrientationNatural();

        Thread.sleep(100);

        onView(withId(R.id.start)).perform(click());

        onView(withId(R.id.pulsator)).check(matches(isDisplayed()));
    }

    private Matcher<? super View> withProgress(final int expectedProgress) {
        return new BoundedMatcher<View, SeekBar>(SeekBar.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with progress: ").appendValue(expectedProgress);
            }

            @Override
            protected boolean matchesSafely(SeekBar item) {
                return item.getProgress() == expectedProgress;
            }
        };
    }

}
