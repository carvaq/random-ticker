package com.fanstaticapps.randomticker.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonActivity
import timber.log.Timber


class OnAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Received alarm")

        PREFS.currentlyTickerRunning = false

        Intent(context, KlaxonActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            putExtra(KlaxonActivity.EXTRA_TIME_ELAPSED, true)
            context.startActivity(this)
        }

    }

}