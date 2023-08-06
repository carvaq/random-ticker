package com.fanstaticapps.randomticker.helper

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.extensions.EXTRA_BOOKMARK_ID
import com.fanstaticapps.randomticker.receiver.AlarmEndedReceiver
import com.fanstaticapps.randomticker.receiver.CreateAlarmReceiver
import com.fanstaticapps.randomticker.ui.cancel.CancelActivity
import com.fanstaticapps.randomticker.ui.main.MainActivity

object IntentHelper {

    fun getMainActivity(context: Context, bookmarkId: Long?): Intent {
        return Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(EXTRA_BOOKMARK_ID, bookmarkId)
        }
    }

    fun getCancelActionPendingIntent(
        context: Context,
        requestCode: Int,
        bookmarkId: Long?
    ): PendingIntent {
        return PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, CancelActivity::class.java).apply {
                putExtra(EXTRA_BOOKMARK_ID, bookmarkId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT.asImmutable
        )
    }

    fun getOpenAppPendingIntent(
        context: Context,
        requestCode: Int,
        bookmarkId: Long?
    ): PendingIntent {
        return PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, MainActivity::class.java).apply {
                putExtra(EXTRA_BOOKMARK_ID, bookmarkId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT.asImmutable
        )
    }

    fun getAlarmEndedReceiverPendingIntent(context: Context, bookmarkId: Long?): PendingIntent? {
        return getAlarmEndedReceiverPendingIntent(
            context,
            PendingIntent.FLAG_UPDATE_CURRENT,
            bookmarkId
        )
    }

    fun getAlarmEndedReceiverCancelPendingIntent(
        context: Context,
        bookmarkId: Long?
    ): PendingIntent? {
        return getAlarmEndedReceiverPendingIntent(
            context,
            PendingIntent.FLAG_CANCEL_CURRENT,
            bookmarkId
        )
    }

    private fun getAlarmEndedReceiverPendingIntent(
        context: Context,
        flag: Int,
        bookmarkId: Long?
    ): PendingIntent? {
        val intent = Intent("com.fanstaticapps.randomticker.ALARM").apply {
            component = ComponentName(context, AlarmEndedReceiver::class.java)
            putExtra(EXTRA_BOOKMARK_ID, bookmarkId)
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ALARM,
            intent,
            flag.asImmutable
        )
    }

    fun getRepeatReceiverPendingIntent(context: Context): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_REPEAT,
            Intent(context, CreateAlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT.asImmutable
        )
    }

    private val Int.asImmutable
        get() = this or PendingIntent.FLAG_IMMUTABLE
    private const val REQUEST_CODE_REPEAT = 112
    private const val REQUEST_CODE_ALARM = 421
}