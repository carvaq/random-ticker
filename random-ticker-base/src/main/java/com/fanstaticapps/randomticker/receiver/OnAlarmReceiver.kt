package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.common.activities.KlaxonBaseActivity
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.activities.KlaxonActivity


class OnAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PREFS.currentlyTickerRunning = false
        val klaxonIntent = Intent(context, KlaxonActivity::class.java)
        klaxonIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        klaxonIntent.putExtra(KlaxonBaseActivity.EXTRA_TIME_ELAPSED, true)
        context.startActivity(klaxonIntent)
    }
}