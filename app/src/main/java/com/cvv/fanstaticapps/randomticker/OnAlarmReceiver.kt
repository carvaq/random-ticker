package com.cvv.fanstaticapps.randomticker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cvv.fanstaticapps.randomticker.activities.KlaxonActivityNavigator

class OnAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        prefs.currentlyTickerRunning = false
        val klaxonIntent = KlaxonActivityNavigator(true)
                .flags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .build(context)
        context.startActivity(klaxonIntent)
    }
}