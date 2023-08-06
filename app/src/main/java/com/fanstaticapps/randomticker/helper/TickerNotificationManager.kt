package com.fanstaticapps.randomticker.helper

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.createKlaxonChannel
import com.fanstaticapps.randomticker.extensions.createTimerChannel
import com.fanstaticapps.randomticker.extensions.getNotificationManager
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonActivity
import javax.inject.Inject

class TickerNotificationManager @Inject constructor() {

    fun cancelAllNotifications(context: Context, bookmark: Bookmark) {
        val notificationManager = context.getNotificationManager()
        notificationManager.cancel(bookmark.runningNotificationId())
        notificationManager.cancel(bookmark.klaxonNotificationId())
    }


    internal fun showRunningNotification(context: Context, bookmark: Bookmark) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val alarmPendingIntent =
                IntentHelper.getOpenAppPendingIntent(
                    context,
                    bookmark.runningNotificationId(),
                    bookmark.id
                )
            val cancelAction = getCancelAction(context, bookmark)

            val notification = NotificationCompat.Builder(context, bookmark.runningChannelId())
                .addAction(cancelAction)
                .setContentTitle(bookmark.name)
                .setShowWhen(true)
                .setWhen(bookmark.intervalEnd)
                .setContentIntent(alarmPendingIntent)
                .setChronometerCountDown(true)
                .setUsesChronometer(true)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_stat_timer)
                .setOnlyAlertOnce(false)
                .build()
            notification.flags = notification.flags or Notification.FLAG_INSISTENT

            context.createTimerChannel(bookmark)
            val notificationManager = context.getNotificationManager()
            notificationManager.cancel(bookmark.runningNotificationId())
            notificationManager.notify(bookmark.runningNotificationId(), notification)
        }
    }


    private fun getCancelAction(context: Context, bookmark: Bookmark): NotificationCompat.Action {
        val cancelPendingIntent =
            IntentHelper.getCancelActionPendingIntent(
                context,
                bookmark.runningNotificationId(),
                bookmark.id
            )
        return NotificationCompat.Action(
            R.drawable.ic_action_stop_timer,
            context.getString(android.R.string.cancel),
            cancelPendingIntent
        )
    }

    private fun getStopAction(context: Context, bookmark: Bookmark): NotificationCompat.Action {
        val cancelPendingIntent =
            IntentHelper.getCancelActionPendingIntent(
                context,
                bookmark.klaxonNotificationId(),
                bookmark.id
            )
        return NotificationCompat.Action(
            R.drawable.ic_action_stop_timer,
            context.getString(R.string.action_stop),
            cancelPendingIntent
        )
    }

    private fun getRepeatAction(context: Context): NotificationCompat.Action {
        val pendingIntent = IntentHelper.getRepeatReceiverPendingIntent(context)
        return NotificationCompat.Action(
            R.drawable.ic_action_repeat_timer,
            context.getString(R.string.action_repeat),
            pendingIntent
        )
    }

    fun showNotificationWithFullScreenIntent(context: Context, bookmark: Bookmark) {
        val notification = NotificationCompat.Builder(context, bookmark.klaxonChannelId())
            .setSmallIcon(R.drawable.ic_stat_timer)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.notification_timer_runned_out))
            .setCategory(Notification.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(context.getFullScreenIntent(bookmark), true)
            .addAction(getStopAction(context, bookmark))
            .addAction(getRepeatAction(context))
            .build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            context.createKlaxonChannel(bookmark)
            val notificationManager = context.getNotificationManager()
            notificationManager.cancel(bookmark.runningNotificationId())
            notificationManager.notify(bookmark.runningNotificationId(), notification)
        }
    }

    private fun Context.getFullScreenIntent(bookmark: Bookmark): PendingIntent {
        val intent = Intent(this, KlaxonActivity::class.java)
        return PendingIntent.getActivity(
            this,
            bookmark.klaxonNotificationId(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}