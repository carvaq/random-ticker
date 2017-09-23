package com.cvv.fanstaticapps.randomticker.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.min_min)
    NumberPicker minMin;
    @BindView(R.id.min_sec)
    NumberPicker minSec;
    @BindView(R.id.max_min)
    NumberPicker maxMin;
    @BindView(R.id.max_sec)
    NumberPicker maxSec;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

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
        perpareNumberPicker(minMin, preferences.getMinMin());
        perpareNumberPicker(minSec, preferences.getMinSec());
        perpareNumberPicker(maxMin, preferences.getMaxMin());
        perpareNumberPicker(maxSec, preferences.getMaxSec());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private void perpareNumberPicker(NumberPicker numberPicker, int startValue) {
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

    private int getTotalValueInMillis(NumberPicker minute, NumberPicker second) {
        return (60 * minute.getValue() + second.getValue()) * 1000;
    }
}
