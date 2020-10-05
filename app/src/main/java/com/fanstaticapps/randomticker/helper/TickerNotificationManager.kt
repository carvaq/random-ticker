package com.fanstaticapps.randomticker.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.getFormattedElapsedMilliseconds
import com.fanstaticapps.randomticker.extensions.getNotificationManager
import com.fanstaticapps.randomticker.extensions.isAtLeastAndroid26
import javax.inject.Inject

class TickerNotificationManager @Inject constructor(private val userPreferences: UserPreferences) {

    fun cancelAllNotifications(context: Context) {
        val notificationManager = context.getNotificationManager()
        notificationManager.cancel(RUNNING_NOTIFICATION_ID)
        notificationManager.cancel(FOREGROUND_NOTIFICATION_ID)
    }

    fun showKlaxonNotification(context: Context, bookmark: Bookmark) {
        cancelAllNotifications(context)
        val notificationManager = context.getNotificationManager()

        if (isAtLeastAndroid26()) {
            val name = context.getString(R.string.foreground_channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance).apply {
                setBypassDnd(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                if (!userPreferences.alarmRingtone.isBlank()) {
                    val audioAttributes = AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                            .build()
                    setSound(userPreferences.alarmRingtone.toUri(), audioAttributes)
                }
                if (userPreferences.vibrationEnabled) {
                    enableVibration(true)
                    vibrationPattern = VIBRATION_PATTERN
                }
            }

            notificationManager.createNotificationChannel(channel)
        }

        val notification = buildKlaxonNotification(context, bookmark)

        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun buildKlaxonNotification(context: Context, bookmark: Bookmark): Notification {
        val contentIntent = IntentHelper.getContentPendingIntent(context, FOREGROUND_NOTIFICATION_ID, true)
        val fullscreenIntent = IntentHelper.getFullscreenPendingIntent(context, FOREGROUND_NOTIFICATION_ID)

        val notificationBuilder =
                NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_timer)
                        .setAutoCancel(true)
                        .setContentTitle(bookmark.name)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .addAction(getStopAction(context))
                        .addAction(getRepeatAction(context))
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setLocalOnly(true)
                        .setFullScreenIntent(fullscreenIntent, true)
                        .setContentIntent(contentIntent)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (!userPreferences.alarmRingtone.isBlank()) {
            notificationBuilder.setSound(userPreferences.alarmRingtone.toUri())
        }
        if (userPreferences.vibrationEnabled) {
            notificationBuilder.setVibrate(VIBRATION_PATTERN)
        }
        return notificationBuilder.build()
    }


    internal fun showRunningNotification(context: Context) {
        cancelAllNotifications(context)

        val notification = buildRunningNotification(context)

        if (isAtLeastAndroid26()) {
            val notificationManager = context.getNotificationManager()
            val name = context.getString(R.string.running_channel_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(RUNNING_CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(channel)
        }

        context.getNotificationManager().notify(RUNNING_NOTIFICATION_ID, notification)
    }

    private fun buildRunningNotification(context: Context): Notification {
        val interval = userPreferences.interval
        val intervalFinished = userPreferences.intervalWillBeFinished
        val alarmPendingIntent = IntentHelper.getContentPendingIntent(context, RUNNING_NOTIFICATION_ID, false)
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
        val cancelPendingIntent = IntentHelper.getCancelActionPendingIntent(context, RUNNING_NOTIFICATION_ID)
        return NotificationCompat.Action(R.drawable.ic_action_stop_timer, context.getString(android.R.string.cancel), cancelPendingIntent)
    }

    private fun getStopAction(context: Context): NotificationCompat.Action {
        val cancelPendingIntent = IntentHelper.getCancelActionPendingIntent(context, FOREGROUND_NOTIFICATION_ID)
        return NotificationCompat.Action(R.drawable.ic_action_stop_timer, context.getString(R.string.action_stop), cancelPendingIntent)
    }

    private fun getRepeatAction(context: Context): NotificationCompat.Action {
        val pendingIntent = IntentHelper.getRepeatReceiverPendingIntent(context)
        return NotificationCompat.Action(R.drawable.ic_action_repeat_timer, context.getString(R.string.action_repeat), pendingIntent)
    }

    companion object {
        const val RUNNING_CHANNEL_ID = "RandomTickerChannel:01"
        const val FOREGROUND_CHANNEL_ID = "RandomTickerChannel:02"
        const val RUNNING_NOTIFICATION_ID = 2312
        const val FOREGROUND_NOTIFICATION_ID = 1243
        private val VIBRATION_PATTERN = longArrayOf(0, 1000, 1000, 1000, 1000, 1000)
    }
}
