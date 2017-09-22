package com.cvv.fanstaticapps.randomticker.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper;

import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class MainActivity extends BaseActivity {
    @BindView(R.id.min_min)
    EditText minMin;
    @BindView(R.id.min_sec)
    EditText minSec;
    @BindView(R.id.max_min)
    EditText maxMin;
    @BindView(R.id.max_sec)
    EditText maxSec;

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
        minMin.setText(String.valueOf(preferences.getMinMin()));
        minSec.setText(String.valueOf(preferences.getMinSec()));
        maxMin.setText(String.valueOf(preferences.getMaxMin()));
        maxSec.setText(String.valueOf(preferences.getMaxSec()));
    }

    private void startAlarmActivity() {
        startActivity(new AlarmActivityNavigator(false, false).build(this));
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
            timerHelper.createNotificationAndAlarm(this, interval, intervalFinished);
            startAlarmActivity();
        } else {
            toast(R.string.error_min_is_bigger_than_max);
        }
    }

    @OnEditorAction(R.id.max_sec)
    boolean onNextClicked(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            createTimer();
            return true;
        } else {
            return false;
        }
    }

    private void saveToPreferences(long interval, long intervalFinished) {
        preferences.edit()
                .setMaxMin(getIntValue(maxMin))
                .setMinMin(getIntValue(minMin))
                .setMaxSec(getIntValue(maxSec))
                .setMinSec(getIntValue(minSec))
                .setCurrentlyTickerRunning(true)
                .setInterval(interval)
                .setIntervalFinished(intervalFinished)
                .apply();
    }

    private int getTotalValueInMillis(EditText minute, EditText second) {
        return (60 * getIntValue(minute) + getIntValue(second)) * 1000;
    }

    private int getIntValue(EditText editText) {
        CharSequence value = editText.getText();
        if (TextUtils.isEmpty(value)) {
            return 0;
        } else {
            return Integer.valueOf(value.toString());
        }
    }
}
