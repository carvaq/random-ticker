package com.cvv.fanstaticapps.randomticker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cvv.fanstaticapps.randomticker.activities.AlarmActivityNavigator;
import com.cvv.fanstaticapps.randomticker.helper.PrefUserSettings;

public class OnAlarmReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the MainActivity
        Intent alarmIntent = new AlarmActivityNavigator(true)
                .build(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        PrefUserSettings preferences = new PrefUserSettings(prefs);
        preferences.setCurrentlyTickerRunning(false);
        context.startActivity(alarmIntent);
    }
}