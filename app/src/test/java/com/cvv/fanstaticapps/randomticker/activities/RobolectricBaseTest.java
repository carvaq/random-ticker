package com.cvv.fanstaticapps.randomticker.activities;

import android.app.Activity;
import android.view.View;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import androidx.annotation.IdRes;

/**
 * Created by Carla
 * Date: 05/02/2018
 * Project: RandomTicker
 */
@RunWith(RobolectricTestRunner.class)
public abstract class RobolectricBaseTest<A extends Activity> {

    Activity activity;

    private final Class<A> activityClass;

    RobolectricBaseTest(Class<A> activityClass) {
        this.activityClass = activityClass;
    }

    @Before
    public void setUp() throws Exception {
        startActivity();
    }

    private void startActivity() {
        activity = Robolectric.setupActivity(activityClass);
    }

    <V extends View> V getView(@IdRes int res) {
        return activity.findViewById(res);
    }
}