package com.cvv.fanstaticapps.randomticker.activities;

import android.content.Intent;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.helper.TimeSeekBarChangeListener;

import org.junit.Test;
import org.robolectric.shadows.ShadowApplication;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by Carla
 * Date: 05/02/2018
 * Project: RandomTicker
 */
public class MainActivityTest extends RobolectricBaseTest<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Test
    public void testTimer_NotCreatedWithMinBiggerThanMax() throws Exception {
        setSeekBarValues(11, 4, 0, 0);

        clickStartButton();

        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
        assertNull(actual);
    }

    @Test
    public void testTimer_CreatedWithMinSmallerThanMax() throws Exception {
        setSeekBarValues(2, 3, 10, 59);

        clickStartButton();

        Intent expectedIntent = new Intent(activity, KlaxonActivity.class);
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());

    }

    private void clickStartButton() {
        getView(R.id.start).performClick();
    }

    private void setSeekBarValues(int minMinutes, int minSeconds, int maxMinutes, int maxSeconds) {
        SeekBar minMinSeekBar = getView(R.id.min_min);
        SeekBar minSecSeekBar = getView(R.id.min_sec);
        SeekBar maxMinSeekBar = getView(R.id.max_min);
        SeekBar maxSecSeekBar = getView(R.id.max_sec);
        maxMinSeekBar.setProgress(maxMinutes);
        maxSecSeekBar.setProgress(maxSeconds);
        minMinSeekBar.setProgress(minMinutes);
        minSecSeekBar.setProgress(minSeconds);

        TextView minDisplayText = getView(R.id.min_value);
        TextView maxDisplayText = getView(R.id.max_value);
        String expectedMinValue = formatExpectedValue(minMinutes, minSeconds);
        String expectedMaxValue = formatExpectedValue(maxMinutes, maxSeconds);

        assertEquals(expectedMinValue, minDisplayText.getText().toString());
        assertEquals(expectedMaxValue, maxDisplayText.getText().toString());
    }

    private String formatExpectedValue(int minutes, int seconds) {
        return String.format(Locale.getDefault(), TimeSeekBarChangeListener.TIME_FORMAT, minutes, seconds);
    }

}