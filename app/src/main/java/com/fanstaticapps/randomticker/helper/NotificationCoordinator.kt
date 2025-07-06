package com.fanstaticapps.randomticker.helper

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.media.AudioManager
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.createNotificationChannel
import com.fanstaticapps.randomticker.extensions.deleteChannel
import com.fanstaticapps.randomticker.extensions.getNotificationManager
import com.fanstaticapps.randomticker.extensions.needsPostNotificationPermission
import com.fanstaticapps.randomticker.helper.IntentHelper.getCancelActionPendingIntent
import com.fanstaticapps.randomticker.helper.IntentHelper.getKlaxonActivityPendingIntent
import com.fanstaticapps.randomticker.helper.IntentHelper.getOpenAppPendingIntent

class NotificationCoordinator(private val context: Context) {
    private val groupKey = "com.fanstaticapps.randomticker.KLAXON"

    fun cancelAllNotifications(bookmark: Bookmark) {
        val notificationManager = context.getNotificationManager()
        notificationManager.cancel(bookmark.runningNotificationId)
        notificationManager.cancel(bookmark.klaxonNotificationId)
    }

    fun deleteChannelsForBookmark(bookmark: Bookmark) {
        context.deleteChannel(bookmark)
    }

    fun triggerNotificationChannelNotification(bookmark: Bookmark) {
        context.createNotificationChannel(bookmark)
        NotificationCompat.Builder(context, bookmark.notificationChannelId)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSmallIcon(R.drawable.ic_stat_timer)
            .setSilent(true)
            .build()
            .show(bookmark.klaxonNotificationId)
        val notificationManager = context.getNotificationManager()
        notificationManager.cancel(bookmark.klaxonNotificationId)
    }

    internal fun showRunningNotification(bookmark: Bookmark) {
        context.createNotificationChannel(bookmark)
        val notification = NotificationCompat.Builder(context, bookmark.notificationChannelId)
            .addAction(
                getCancelAction(
                    bookmark,
                    android.R.string.cancel
                )
            )
            .setContentTitle(bookmark.name)
            .setGroup(groupKey)
            .setContentIntent(getOpenAppPendingIntent(context, bookmark))
            .setSilent(true)
            .setProgress(0, 100, true)
            .setChronometerCountDown(false)
            .setUsesChronometer(true)
            .setSmallIcon(R.drawable.ic_stat_timer)
            .build()

        notification.show(bookmark.runningNotificationId)
    }


    fun showKlaxonNotification(bookmark: Bookmark) {
        val sound = bookmark.soundUri?.toUri()
        val channel = context.createNotificationChannel(bookmark)
        val builder = NotificationCompat.Builder(context, bookmark.notificationChannelId)
            .setSmallIcon(R.drawable.ic_stat_timer)
            .setContentTitle(bookmark.name)
            .setContentText(context.getString(R.string.notification_ticker_ended))
            .setCategory(Notification.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(sound)
            .setFullScreenIntent(getKlaxonActivityPendingIntent(context, bookmark), true)
            .addAction(getCancelAction(bookmark, R.string.action_stop))

        if (!bookmark.autoRepeat) {
            builder.addAction(getRepeatAction(context, bookmark))
        }
        channel?.let { builder.setSound(channel.sound, AudioManager.STREAM_ALARM) }

        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT
        notification.show(bookmark.klaxonNotificationId)
    }

    private fun getCancelAction(
        bookmark: Bookmark,
        actionResId: Int
    ): NotificationCompat.Action {
        return NotificationCompat.Action(
            R.drawable.ic_action_stop_timer,
            context.getString(actionResId),
            getCancelActionPendingIntent(context, bookmark)
        )
    }

    private fun getRepeatAction(context: Context, bookmark: Bookmark): NotificationCompat.Action {
        return NotificationCompat.Action(
            R.drawable.ic_action_repeat_timer,
            context.getString(R.string.action_repeat),
            IntentHelper.getRepeatReceiverPendingIntent(context, bookmark)
        )
    }

    @SuppressLint("MissingPermission")
    private fun Notification.show(notificationId: Int) {
        val notificationManager = context.getNotificationManager()
        notificationManager.cancel(notificationId)
        if (!context.needsPostNotificationPermission()) {
            notificationManager.notify(notificationId, this)
        }
    }

}