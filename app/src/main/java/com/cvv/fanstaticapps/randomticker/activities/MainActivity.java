package com.cvv.fanstaticapps.randomticker.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;

import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.min_min)
    ScrollableNumberPicker minMin;
    @BindView(R.id.min_sec)
    ScrollableNumberPicker minSec;
    @BindView(R.id.max_min)
    ScrollableNumberPicker maxMin;
    @BindView(R.id.max_sec)
    ScrollableNumberPicker maxSec;

    @Inject
    TimerHelper timerHelper;

    private Random randomGenerator = new Random(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (preferences.isCurrentlyTickerRunning()) {
            startAlarmActivity();
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        prepareNumberPicker(minMin, preferences.getMinMin());
        prepareNumberPicker(minSec, preferences.getMinSec());
        prepareNumberPicker(maxMin, preferences.getMaxMin());
        prepareNumberPicker(maxSec, preferences.getMaxSec());
    }

    private void prepareNumberPicker(ScrollableNumberPicker numberPicker, int startValue) {
        numberPicker.setValue(startValue);
    }

    private void startAlarmActivity() {
        startActivity(new KlaxonActivityNavigator(false).build(this));
        finish();
    }

    @OnClick(R.id.start)
    void onStartClicked() {
        createTimer();
    }

    private void createTimer() {
        int min = getTotalValueInMillis(minMin, minSec);
        int max = getTotalValueInMillis(maxMin, maxSec);
        if (max > min) {
            long interval = randomGenerator.nextInt((max - min) + 1) + min;
            long intervalFinished = System.currentTimeMillis() + interval;
            saveToPreferences(interval, intervalFinished);
            timerHelper.createNotificationAndAlarm(this, preferences);
            startAlarmActivity();
        } else {
            toast(R.string.error_min_is_bigger_than_max);
        }
    }

    private void saveToPreferences(long interval, long intervalFinished) {
        preferences.edit()
                .setMaxMin(maxMin.getValue())
                .setMinMin(minMin.getValue())
                .setMaxSec(maxSec.getValue())
                .setMinSec(minSec.getValue())
                .setCurrentlyTickerRunning(true)
                .setInterval(interval)
                .setIntervalFinished(intervalFinished)
                .apply();
    }

    private int getTotalValueInMillis(ScrollableNumberPicker minute, ScrollableNumberPicker second) {
        return (60 * minute.getValue() + second.getValue()) * 1000;
    }
}
