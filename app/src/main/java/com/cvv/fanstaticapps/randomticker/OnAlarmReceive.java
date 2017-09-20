package com.cvv.fanstaticapps.randomticker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cvv.fanstaticapps.randomticker.activities.AlarmActivityNavigator;

public class OnAlarmReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the MainActivity
        Intent alarmIntent = new AlarmActivityNavigator(true, 0, true)
                .flags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .build(context);
        context.startActivity(alarmIntent);
    }
}