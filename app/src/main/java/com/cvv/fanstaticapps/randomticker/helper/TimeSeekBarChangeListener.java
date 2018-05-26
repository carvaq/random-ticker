package com.cvv.fanstaticapps.randomticker.helper;

import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.VisibleForTesting;

import static java.lang.Math.exp;
import static java.lang.Math.log;

/**
 * Created by Carla
 * Date: 04/02/2018
 * Project: RandomTicker
 */

public class TimeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

	private static final int MIN_VALUE = 1;
	private static final int MAX_VALUE = 59;
	private static final double LOG_MIN_VALUE = log(MIN_VALUE);
	private static final double LOG_MAX_VALUE = log(MAX_VALUE);
	private static final double FACTOR = (LOG_MAX_VALUE - LOG_MIN_VALUE) / (MAX_VALUE - MIN_VALUE);

	@VisibleForTesting
	public static final String TIME_FORMAT = "%02dm %02ds";

	private final TextView displayTextView;
	private final SeekBar minuteSeekbar;
	private final SeekBar secondsSeekbar;

	private int minutes;
	private int seconds;

	public TimeSeekBarChangeListener(TextView displayTextView,
	                                 SeekBar minuteSeekbar,
	                                 SeekBar secondsSeekbar) {
		this.displayTextView = displayTextView;
		this.minuteSeekbar = minuteSeekbar;
		this.secondsSeekbar = secondsSeekbar;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int minutesProgress = minuteSeekbar.getProgress();
		//Formula from https://plus.google.com/+AladinQ/posts/2B1fpKQWFmG
		minutes = (int) exp(LOG_MIN_VALUE + (minutesProgress - MIN_VALUE) * FACTOR);
		seconds = secondsSeekbar.getProgress();
		String displayText = String.format(Locale.getDefault(), TIME_FORMAT, minutes, seconds);
		displayTextView.setText(displayText);


	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
}
