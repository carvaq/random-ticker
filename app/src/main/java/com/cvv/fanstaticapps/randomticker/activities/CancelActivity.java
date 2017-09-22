package com.cvv.fanstaticapps.randomticker.activities;

import android.os.Bundle;

import com.cvv.fanstaticapps.randomticker.helper.TimerHelper;

import javax.inject.Inject;

public class CancelActivity extends BaseActivity {
    @Inject
    TimerHelper timerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerHelper.cancelNotificationAndGoBack(this, preferences);
    }
}
