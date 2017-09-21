package com.cvv.fanstaticapps.randomticker.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper;

import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

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
        setContentView(R.layout.activity_main);
    }

    @OnClick(R.id.start)
    void onStartClicked() {
        int min = getTotalValueInMillis(minMin, minSec);
        int max = getTotalValueInMillis(maxMin, maxSec);
        if (max > min) {
            long interval = randomGenerator.nextInt((max - min) + 1) + min;
            long intervalFinished = System.currentTimeMillis() + interval;
            timerHelper.createNotificationAndAlarm(this, interval, intervalFinished);
            startActivity(new AlarmActivityNavigator(false, intervalFinished, false).build(this));
            finish();
        } else {
            toast(R.string.error_min_is_bigger_than_max);
        }
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
