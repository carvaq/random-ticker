package com.cvv.fanstaticapps.randomticker;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;
import android.widget.SeekBar;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Carla
 * Date: 05/02/2018
 * Project: RandomTicker
 */
public class SetProgressAction implements ViewAction {
    private final int progress;

    public SetProgressAction(int progress) {
        this.progress = progress;
    }

    @Override
    public Matcher<View> getConstraints() {
        return allOf(isDisplayed(), isAssignableFrom(SeekBar.class));
    }

    @Override
    public String getDescription() {
        return "set progress";
    }

    @Override
    public void perform(UiController uiController, View view) {
        ((SeekBar) view).setProgress(progress);
    }

    public static SetProgressAction setProgress(int progress) {
        return new SetProgressAction(progress);
    }
}
