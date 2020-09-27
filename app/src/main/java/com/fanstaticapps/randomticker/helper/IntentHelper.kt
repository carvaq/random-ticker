package com.fanstaticapps.randomticker.helper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.receiver.AlarmReceiver
import com.fanstaticapps.randomticker.receiver.RepeatAlarmReceiver
import com.fanstaticapps.randomticker.ui.CancelActivity
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonActivity
import com.fanstaticapps.randomticker.ui.main.MainActivity
import javax.inject.Inject

class IntentHelper @Inject constructor() {

    fun getMainActivity(context: Context): Intent {
        return Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
    }

    fun getKlaxonActivity(context: Context, hasTimeElapsed: Boolean): Intent {
        val intent = Intent(context, KlaxonActivity::class.java)
        intent.putExtra(KlaxonActivity.EXTRA_TIME_ELAPSED, hasTimeElapsed)
        return intent
    }

    fun getContentPendingIntent(context: Context, requestCode: Int, hasTimeElapsed: Boolean): PendingIntent {
        val intent = getKlaxonActivity(context, hasTimeElapsed).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getFullscreenPendingIntent(context: Context, requestCode: Int): PendingIntent {
        val intent = getKlaxonActivity(context, true).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getCancelActionPendingIntent(context: Context, requestCode: Int): PendingIntent {
        return PendingIntent.getActivity(context, requestCode, Intent(context, CancelActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getAlarmReceiveAsPendingIntent(context: Context): PendingIntent {
        return PendingIntent.getBroadcast(context, 123, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getRepeatReceiverPendingIntent(context: Context): PendingIntent {
        return PendingIntent.getBroadcast(context, 112, Intent(context, RepeatAlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
    }
}