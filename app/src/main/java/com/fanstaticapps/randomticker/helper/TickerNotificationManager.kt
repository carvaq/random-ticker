package com.fanstaticapps.randomticker.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.getFormattedElapsedMilliseconds
import com.fanstaticapps.randomticker.extensions.getNotificationManager
import com.fanstaticapps.randomticker.extensions.isAtLeastAndroid26
import javax.inject.Inject

class TickerNotificationManager @Inject constructor(private val intentHelper: IntentHelper) {

    internal fun showRunningNotification(context: Context) {
        val notification = getRunningNotification(context)

        if (isAtLeastAndroid26()) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = context.getString(R.string.running_channel_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(RUNNING_CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(channel)
        }

        context.getNotificationManager().notify(RUNNING_NOTIFICATION_ID, notification)
    }


    fun cancelNotifications(context: Context) {
        PREFS.currentlyTickerRunning = false

        val notificationManager = context.getNotificationManager()
        notificationManager.cancel(RUNNING_NOTIFICATION_ID)
        notificationManager.cancel(FOREGROUND_NOTIFICATION_ID)
    }

    private fun getKlaxonNotification(context: Context, bookmark: Bookmark): Notification {
        val contentIntent = intentHelper.getContentPendingIntent(context, FOREGROUND_NOTIFICATION_ID, true)
        val fullscreenIntent = intentHelper.getFullscreenPendingIntent(context, FOREGROUND_NOTIFICATION_ID)

        val notificationBuilder =
                NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_timer)
                        .setAutoCancel(true)
                        .setContentTitle(bookmark.name)
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .addAction(getStopAction(context))
                        .addAction(getRepeatAction(context))
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setLocalOnly(true)
                        .setFullScreenIntent(fullscreenIntent, true)
                        .setContentIntent(contentIntent)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        return notificationBuilder.build()
    }

    fun showKlaxonNotification(context: Context, bookmark: Bookmark) {
        val notificationManager = context.getNotificationManager()

        if (isAtLeastAndroid26()) {
            val name = context.getString(R.string.foreground_channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance)
            channel.setBypassDnd(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.deleteNotificationChannel(FOREGROUND_CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = getKlaxonNotification(context, bookmark)

        cancelNotifications(context)

        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notification)
        AlarmKlaxon.start(context)
    }

    private fun getRunningNotification(context: Context): Notification {
        val interval = PREFS.interval
        val intervalFinished = PREFS.intervalFinished
        val alarmPendingIntent = intentHelper.getContentPendingIntent(context, RUNNING_NOTIFICATION_ID, false)
        val cancelAction = getCancelAction(context)

        val formattedInterval = getFormattedElapsedMilliseconds(interval)
        val builder = NotificationCompat.Builder(context, RUNNING_CHANNEL_ID)
                .addAction(cancelAction)
                .setContentTitle(context.getString(R.string.notification_title, formattedInterval))
                .setContentText(getFormattedElapsedMilliseconds(intervalFinished - System.currentTimeMillis()))
                .setAutoCancel(false)
                .setShowWhen(true)
                .setContentIntent(alarmPendingIntent)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_stat_timer)

        return builder.build()
    }

    private fun getCancelAction(context: Context): NotificationCompat.Action {
        val cancelPendingIntent = intentHelper.getCancelActionPendingIntent(context, RUNNING_NOTIFICATION_ID)
        return NotificationCompat.Action(R.drawable.ic_action_stop_timer, context.getString(android.R.string.cancel), cancelPendingIntent)
    }

    private fun getStopAction(context: Context): NotificationCompat.Action {
        val cancelPendingIntent = intentHelper.getCancelActionPendingIntent(context, FOREGROUND_NOTIFICATION_ID)
        return NotificationCompat.Action(R.drawable.ic_action_stop_timer, context.getString(R.string.action_stop), cancelPendingIntent)
    }

    private fun getRepeatAction(context: Context): NotificationCompat.Action {
        val pendingIntent = intentHelper.getRepeatReceiverPendingIntent(context)
        return NotificationCompat.Action(R.drawable.ic_action_repeat_timer, context.getString(R.string.action_repeat), pendingIntent)
    }

    companion object {
        const val RUNNING_CHANNEL_ID = "RandomTickerChannel:01"
        const val FOREGROUND_CHANNEL_ID = "RandomTickerChannel:02"
        const val RUNNING_NOTIFICATION_ID = 2312
        const val FOREGROUND_NOTIFICATION_ID = 1243
        val VIBRATION_PATTERN = longArrayOf(0, 100, 800, 600, 800, 800, 800, 1000)
    }
}
