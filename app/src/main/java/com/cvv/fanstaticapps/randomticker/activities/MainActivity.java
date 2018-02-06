package com.cvv.fanstaticapps.randomticker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cvv.fanstaticapps.randomticker.LicenseActivity;
import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.helper.TimeSeekBarChangeListener;
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper;

import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.min_value)
    TextView minValue;
    @BindView(R.id.max_value)
    TextView maxValue;
    @BindView(R.id.min_min)
    SeekBar minMin;
    @BindView(R.id.min_sec)
    SeekBar minSec;
    @BindView(R.id.max_min)
    SeekBar maxMin;
    @BindView(R.id.max_sec)
    SeekBar maxSec;

    @Inject
    TimerHelper timerHelper;

    private Random randomGenerator = new Random(System.currentTimeMillis());
    private TimeSeekBarChangeListener minValuesSeekBarListener;
    private TimeSeekBarChangeListener maxValuesSeekBarListener;

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
        setSupportActionBar(toolbar);


        minValuesSeekBarListener =
                new TimeSeekBarChangeListener(minValue, minMin, minSec);
        maxValuesSeekBarListener =
                new TimeSeekBarChangeListener(maxValue, maxMin, maxSec);

        prepareValueSelectionView(minMin, preferences.getMinMin(), minValuesSeekBarListener);
        prepareValueSelectionView(minSec, preferences.getMinSec(), minValuesSeekBarListener);
        prepareValueSelectionView(maxMin, preferences.getMaxMin(), maxValuesSeekBarListener);
        prepareValueSelectionView(maxSec, preferences.getMaxSec(), maxValuesSeekBarListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_licenses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.licenses) {
            startActivity(new Intent(this, LicenseActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareValueSelectionView(SeekBar seekBar, int startValue,
                                           TimeSeekBarChangeListener seekBarChangeListener) {
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar.setMax(59);

        //This is for the configuration change case
        if (seekBar.getProgress() == 0) {
            seekBar.setProgress(startValue);
        }
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
        int min = getTotalValueInMillis(minValuesSeekBarListener);
        int max = getTotalValueInMillis(maxValuesSeekBarListener);
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

    private int getTotalValueInMillis(TimeSeekBarChangeListener seekBarChangeListener) {
        return (60 * seekBarChangeListener.getMinutes() + seekBarChangeListener.getSeconds()) * 1000;
    }

    private void saveToPreferences(long interval, long intervalFinished) {
        preferences.edit()
                .setMaxMin(maxMin.getProgress())
                .setMinMin(minMin.getProgress())
                .setMaxSec(maxSec.getProgress())
                .setMinSec(minSec.getProgress())
                .setCurrentlyTickerRunning(true)
                .setInterval(interval)
                .setIntervalFinished(intervalFinished)
                .apply();
    }
}
