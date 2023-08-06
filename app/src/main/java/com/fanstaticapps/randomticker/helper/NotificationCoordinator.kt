package com.fanstaticapps.randomticker.helper

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.createKlaxonChannel
import com.fanstaticapps.randomticker.extensions.createTimerChannel
import com.fanstaticapps.randomticker.extensions.getKlaxonChannel
import com.fanstaticapps.randomticker.extensions.getNotificationManager
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonActivity
import javax.inject.Inject

class NotificationCoordinator @Inject constructor() {

    fun cancelAllNotifications(context: Context, bookmark: Bookmark) {
        val notificationManager = context.getNotificationManager()
        notificationManager.cancel(bookmark.runningNotificationId())
        notificationManager.cancel(bookmark.klaxonNotificationId())
    }


    internal fun showRunningNotification(context: Context, bookmark: Bookmark) {
        context.createTimerChannel(bookmark)

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
            .setSmallIcon(R.drawable.ic_stat_timer)
            .build()

        notification.show(context, bookmark.runningNotificationId())
    }


    fun showNotificationWithFullScreenIntent(context: Context, bookmark: Bookmark) {
        context.createKlaxonChannel(bookmark)
        val channel = context.getKlaxonChannel(bookmark)
        val builder = NotificationCompat.Builder(context, bookmark.klaxonChannelId())
            .setSmallIcon(R.drawable.ic_stat_timer)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.notification_ticker_ended))
            .setCategory(Notification.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(context.getFullScreenIntent(bookmark), true)
            .addAction(getStopAction(context, bookmark))
            .addAction(getRepeatAction(context))

        channel?.let {
            builder.setSound(channel.sound, AudioManager.STREAM_ALARM)
        }

        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        notification.show(context, bookmark.runningNotificationId())
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
        return NotificationCompat.Action(
            R.drawable.ic_action_repeat_timer,
            context.getString(R.string.action_repeat),
            IntentHelper.getRepeatReceiverPendingIntent(context)
        )
    }

    private fun Notification.show(context: Context, notificationId: Int) {
        val notificationManager = context.getNotificationManager()
        notificationManager.cancel(notificationId)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(notificationId, this)
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