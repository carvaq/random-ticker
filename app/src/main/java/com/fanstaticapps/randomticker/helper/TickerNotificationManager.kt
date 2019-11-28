package com.fanstaticapps.randomticker.helper

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.R
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

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(intentHelper.getAlarmReceiveAsPendingIntent(context))

        val notificationManager = context.getNotificationManager()
        notificationManager.cancel(RUNNING_NOTIFICATION_ID)
        notificationManager.cancel(FOREGROUND_NOTIFICATION_ID)
    }

    private fun getKlaxonNotification(context: Context): Notification {
        val contentIntent = intentHelper.getContentPendingIntent(context, FOREGROUND_NOTIFICATION_ID, true)
        val fullscreenIntent = intentHelper.getFullscreenPendingIntent(context, FOREGROUND_NOTIFICATION_ID)

        val notificationBuilder =
                NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_timer)
                        .setAutoCancel(true)
                        .setContentTitle("Random Ticker")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .addAction(getCancelAction(context, FOREGROUND_NOTIFICATION_ID))
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setLocalOnly(true)
                        .setWhen(0)
                        .setFullScreenIntent(fullscreenIntent, true)
                        .setContentIntent(contentIntent)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)


        return notificationBuilder.build()
    }

    fun showKlaxonNotification(context: Context) {
        val notificationManager = context.getNotificationManager()

        if (isAtLeastAndroid26()) {
            val name = context.getString(R.string.foreground_channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance)
            channel.setBypassDnd(true)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = getKlaxonNotification(context)

        cancelNotifications(context)

        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun getRunningNotification(context: Context): Notification {
        val interval = PREFS.interval
        val intervalFinished = PREFS.intervalFinished
        val alarmPendingIntent = intentHelper.getContentPendingIntent(context, RUNNING_NOTIFICATION_ID, false)
        val cancelAction = getCancelAction(context, RUNNING_NOTIFICATION_ID)

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

    private fun getCancelAction(context: Context, requestCode: Int): NotificationCompat.Action {
        val cancelPendingIntent = intentHelper.getCancelAction(context, requestCode)
        return NotificationCompat.Action(R.drawable.ic_action_stop_timer, context.getString(android.R.string.cancel), cancelPendingIntent)
    }

    companion object {
        private const val RUNNING_CHANNEL_ID = "RandomTickerChannel:01"
        private const val FOREGROUND_CHANNEL_ID = "RandomTickerChannel:02"
        const val RUNNING_NOTIFICATION_ID = 2312
        const val FOREGROUND_NOTIFICATION_ID = 1243
        val VIBRATION_PATTERN = longArrayOf(0, 100, 800, 600, 800, 800, 800, 1000)
    }
}
