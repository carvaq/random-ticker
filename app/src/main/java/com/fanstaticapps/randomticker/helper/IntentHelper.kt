package com.fanstaticapps.randomticker.helper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.alarm.OnAlarmReceiver
import com.fanstaticapps.randomticker.ui.CancelActivity
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonActivity
import com.fanstaticapps.randomticker.ui.main.MainActivity
import javax.inject.Inject

class IntentHelper @Inject constructor() {
    companion object {
        private const val BROADCAST_REQUEST_CODE = 123
        private const val ACTIVITY_REQUEST_CODE = 124
    }

    fun getMainActivity(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }

    fun getKlaxonActivity(context: Context, hasTimeElapsed: Boolean): Intent {
        val intent = Intent(context, KlaxonActivity::class.java)
        intent.putExtra(KlaxonActivity.EXTRA_TIME_ELAPSED, hasTimeElapsed)
        return intent
    }

    fun getAlarmReceiveAsPendingIntent(context: Context): PendingIntent {
        val alarmIntent = Intent(context, OnAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(context, BROADCAST_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getContentPendingIntent(context: Context, hasTimeElapsed: Boolean): PendingIntent {
        val intent = getKlaxonActivity(context, hasTimeElapsed).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return PendingIntent.getActivity(context, ACTIVITY_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getFullscreenPendingIntent(context: Context): PendingIntent {
        val intent = getKlaxonActivity(context, true).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        }
        return PendingIntent.getActivity(context, ACTIVITY_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getCancelAction(context: Context): PendingIntent {
        return PendingIntent.getActivity(context, 0, Intent(context, CancelActivity::class.java), 0)
    }
}