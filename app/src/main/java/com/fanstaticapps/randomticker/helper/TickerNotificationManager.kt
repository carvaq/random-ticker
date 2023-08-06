package com.fanstaticapps.randomticker.helper

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.createKlaxonChannel
import com.fanstaticapps.randomticker.extensions.createTimerChannel
import com.fanstaticapps.randomticker.extensions.getNotificationManager
import com.fanstaticapps.randomticker.extensions.isAtLeastO
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonActivity
import javax.inject.Inject

class TickerNotificationManager @Inject constructor(private val tickerPreferences: TickerPreferences) {

    fun cancelAllNotifications(context: Context) {
        val notificationManager = context.getNotificationManager()
        notificationManager.cancel(RUNNING_NOTIFICATION_ID)
        notificationManager.cancel(FOREGROUND_NOTIFICATION_ID)
    }

    fun showKlaxonNotification(service: Service, bookmark: Bookmark) = with(service) {
        cancelAllNotifications(baseContext)
        service.applicationContext.createKlaxonChannel(bookmark)
        startForeground(FOREGROUND_NOTIFICATION_ID, buildKlaxonNotification(baseContext, bookmark))
    }

    private fun buildKlaxonNotification(context: Context, bookmark: Bookmark): Notification {
        val contentIntent =
            IntentHelper.getContentPendingIntent(context, FOREGROUND_NOTIFICATION_ID)
        val fullscreenIntent =
            IntentHelper.getFullscreenPendingIntent(
                context,
                FOREGROUND_NOTIFICATION_ID,
                bookmark.id
            )

        val notificationBuilder =
            NotificationCompat.Builder(context, bookmark.klaxonChannelId())
                .setSmallIcon(R.drawable.ic_stat_timer)
                .setAutoCancel(true)
                .setContentTitle(bookmark.name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(getStopAction(context, bookmark.id))
                .addAction(getRepeatAction(context))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setLocalOnly(true)
                .setFullScreenIntent(fullscreenIntent, true)
                .setContentIntent(contentIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationChannel = if (isAtLeastO()) {
            context.getNotificationManager().getNotificationChannel(bookmark.name)
        } else {
            null
        }
        val alarmRingtone = if (isAtLeastO()) {
            notificationChannel?.sound
        } else {
            tickerPreferences.alarmRingtone.toUri()
        }
        val vibrationEnabled = if (isAtLeastO()) {
            notificationChannel?.shouldVibrate() ?: false
        } else {
            tickerPreferences.vibrationEnabled
        }
        if (alarmRingtone != null && !alarmRingtone.scheme.isNullOrEmpty()) {
            notificationBuilder.setSound(alarmRingtone)
        }
        if (vibrationEnabled) {
            notificationBuilder.setVibrate(VIBRATION_PATTERN)
        }
        return notificationBuilder.build()
    }


    internal fun showRunningNotification(context: Context, bookmark: Bookmark) {
        val alarmPendingIntent =
            IntentHelper.getOpenAppPendingIntent(context, RUNNING_NOTIFICATION_ID, bookmark.id)
        val cancelAction = getCancelAction(context, bookmark.id)

        val notification = NotificationCompat.Builder(context, bookmark.runningChannelId())
            .addAction(cancelAction)
            .setContentTitle(bookmark.name)
            .setAutoCancel(false)
            .setShowWhen(true)
            .setWhen(bookmark.intervalEnd)
            .setContentIntent(alarmPendingIntent)
            .setUsesChronometer(true)
            .setWhen(System.currentTimeMillis())
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_stat_timer)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            context.createTimerChannel(bookmark)
            val notificationManager = context.getNotificationManager()
            notificationManager.cancel(RUNNING_NOTIFICATION_ID)
            notificationManager.notify(RUNNING_NOTIFICATION_ID, notification)
        }
    }


    private fun getCancelAction(context: Context, bookmarkId: Long?): NotificationCompat.Action {
        val cancelPendingIntent =
            IntentHelper.getCancelActionPendingIntent(context, RUNNING_NOTIFICATION_ID, bookmarkId)
        return NotificationCompat.Action(
            R.drawable.ic_action_stop_timer,
            context.getString(android.R.string.cancel),
            cancelPendingIntent
        )
    }

    private fun getStopAction(context: Context, bookmarkId: Long?): NotificationCompat.Action {
        val cancelPendingIntent =
            IntentHelper.getCancelActionPendingIntent(
                context,
                FOREGROUND_NOTIFICATION_ID,
                bookmarkId
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(context.getFullScreenIntent(), true)
            .addAction(getStopAction(context, bookmark.id))
            .addAction(getRepeatAction(context))
            .build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            context.createKlaxonChannel(bookmark)
            val notificationManager = context.getNotificationManager()
            notificationManager.cancel(RUNNING_NOTIFICATION_ID)
            notificationManager.notify(RUNNING_NOTIFICATION_ID, notification)
        }
    }

    private fun Context.getFullScreenIntent(): PendingIntent {
        val intent = Intent(this, KlaxonActivity::class.java)
        return PendingIntent.getActivity(
            this,
            FULL_SCREEN_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val RUNNING_NOTIFICATION_ID = 2312
        const val FOREGROUND_NOTIFICATION_ID = 1243
        const val FULL_SCREEN_NOTIFICATION_ID = 987
        private val VIBRATION_PATTERN = longArrayOf(0, 1000, 1000, 1000, 1000, 1000)
    }
}