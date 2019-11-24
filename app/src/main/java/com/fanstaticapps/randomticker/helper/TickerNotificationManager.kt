package com.fanstaticapps.randomticker.helper

import android.app.AlarmManager
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
import com.fanstaticapps.randomticker.extensions.getNotificationManager
import com.fanstaticapps.randomticker.extensions.isAtLeastAndroid26
import javax.inject.Inject

class TickerNotificationManager @Inject constructor(private val intentHelper: IntentHelper) {

    internal fun showRunningNotification(context: Context) {
        val notification = getRunningNotification(context)

        if (isAtLeastAndroid26()) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = context.getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(RUNING_CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(channel)
        }

        context.getNotificationManager().notify(RUNNING_NOTIFICATION_ID, notification)
    }


    fun cancelRunningNotification(context: Context) {
        PREFS.currentlyTickerRunning = false

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(intentHelper.getAlarmReceiveAsPendingIntent(context))

        context.getNotificationManager().cancel(RUNNING_NOTIFICATION_ID)
    }

    private fun getKlaxonNotification(context: Context): Notification {
        val uri = Uri.parse(PREFS.alarmRingtone)

        val contentIntent = intentHelper.getContentPendingIntent(context, true)
        val fullscreenIntent = intentHelper.getFullscreenPendingIntent(context)

        val notificationBuilder =
                NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_timer)
                        .setAutoCancel(true)
                        .setContentTitle("Incoming call")
                        .setContentText("(919) 555-1234")
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setSound(uri, AudioManager.STREAM_ALARM)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .addAction(getCancelAction(context))
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setLocalOnly(true)
                        .setWhen(0)
                        .setFullScreenIntent(fullscreenIntent, true)
                        .setOngoing(true)
                        .setContentIntent(contentIntent)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (PREFS.vibrator) {
            notificationBuilder.setVibrate(VIBRATION_PATTERN)
        }

        val notification = notificationBuilder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        return notification
    }

    fun showKlaxongNotification(context: Context) {
        val notificationManager = context.getNotificationManager()

        if (isAtLeastAndroid26()) {
            val name = context.getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance)
            val uri = Uri.parse(PREFS.alarmRingtone)
            val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_ALARM)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .build()
            channel.setSound(uri, audioAttributes)
            channel.enableVibration(PREFS.vibrator)
            channel.setBypassDnd(true)
            channel.vibrationPattern = VIBRATION_PATTERN
            notificationManager.createNotificationChannel(channel)
        }

        val notification = getKlaxonNotification(context)

        cancelRunningNotification(context)

        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun getRunningNotification(context: Context): Notification {
        val interval = PREFS.interval
        val intervalFinished = PREFS.intervalFinished
        val alarmPendingIntent = intentHelper.getContentPendingIntent(context, false)
        val cancelAction = getCancelAction(context)

        val formattedInterval = getFormattedElapsedMilliseconds(interval)
        val builder = NotificationCompat.Builder(context, RUNING_CHANNEL_ID)
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
        val cancelPendingIntent = intentHelper.getCancelAction(context)
        return NotificationCompat.Action(R.drawable.ic_action_stop_timer, context.getString(android.R.string.cancel), cancelPendingIntent)
    }

    companion object {
        private const val RUNNING_NOTIFICATION_ID = 2312
        private const val RUNING_CHANNEL_ID = "RandomTickerChannel:01"
        private const val FOREGROUND_CHANNEL_ID = "RandomTickerChannel:02"
        const val FOREGROUND_NOTIFICATION_ID = 1243
        val VIBRATION_PATTERN = longArrayOf(0, 100, 800, 600, 800, 800, 800, 1000)
    }
}
