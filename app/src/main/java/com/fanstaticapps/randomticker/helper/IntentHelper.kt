package com.fanstaticapps.randomticker.helper

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.extensions.isAtLeastM
import com.fanstaticapps.randomticker.receiver.AlarmReceiver
import com.fanstaticapps.randomticker.receiver.RepeatAlarmReceiver
import com.fanstaticapps.randomticker.ui.CancelActivity
import com.fanstaticapps.randomticker.ui.RingtonePlayingService
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonActivity
import com.fanstaticapps.randomticker.ui.main.MainActivity


object IntentHelper {

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

    fun getContentPendingIntent(
        context: Context,
        requestCode: Int,
        hasTimeElapsed: Boolean
    ): PendingIntent {
        val intent = getKlaxonActivity(context, hasTimeElapsed).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT.asImmutable
        )
    }

    fun getFullscreenPendingIntent(context: Context, requestCode: Int): PendingIntent {
        val intent = getKlaxonActivity(context, true).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT.asImmutable
        )
    }

    fun getCancelActionPendingIntent(context: Context, requestCode: Int): PendingIntent {
        return PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, CancelActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT.asImmutable
        )
    }

    fun getCreateAlarmReceiverAsPendingIntent(context: Context): PendingIntent? {
        return getAlarmReceiverAsPendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getCancelAlarmReceiverAsPendingIntent(context: Context): PendingIntent? {
        return getAlarmReceiverAsPendingIntent(context, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun getAlarmReceiverAsPendingIntent(context: Context, flag: Int): PendingIntent? {
        val intent = Intent("com.fanstaticapps.randomticker.ALARM").apply {
            component = ComponentName(context, AlarmReceiver::class.java)
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ALARM,
            intent,
            flag.asImmutable
        )
    }

    fun getRingtoneServiceIntent(context: Context): Intent {
        return Intent(context, RingtonePlayingService::class.java)
    }

    fun getRepeatReceiverPendingIntent(context: Context): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_REPEAT,
            Intent(context, RepeatAlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT.asImmutable
        )
    }

    private val Int.asImmutable
        get() = if (isAtLeastM()) {
            this or PendingIntent.FLAG_IMMUTABLE
        } else {
            this
        }
    private const val REQUEST_CODE_REPEAT = 112
    private const val REQUEST_CODE_ALARM = 421
}