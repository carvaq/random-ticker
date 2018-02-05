package com.cvv.fanstaticapps.randomticker.helper;

import android.support.annotation.VisibleForTesting;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Carla
 * Date: 04/02/2018
 * Project: RandomTicker
 */

public class TimeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

    @VisibleForTesting
    public static final String TIME_FORMAT = "%02dm %02ds";

    private final TextView displayTextView;
    private final SeekBar minuteSeekbar;
    private final SeekBar secondsSeekbar;

    public TimeSeekBarChangeListener(TextView displayTextView,
                                     SeekBar minuteSeekbar,
                                     SeekBar secondsSeekbar) {
        this.displayTextView = displayTextView;
        this.minuteSeekbar = minuteSeekbar;
        this.secondsSeekbar = secondsSeekbar;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int minutes = minuteSeekbar.getProgress();
        int seconds = secondsSeekbar.getProgress();
        String displayText = String.format(Locale.getDefault(), TIME_FORMAT, minutes, seconds);
        displayTextView.setText(displayText);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
