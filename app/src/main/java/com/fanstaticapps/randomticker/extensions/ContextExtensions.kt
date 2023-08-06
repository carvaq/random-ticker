package com.fanstaticapps.randomticker.extensions

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Vibrator
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark

fun Context.getNotificationManager() = NotificationManagerCompat.from(this)

fun Context.getAlarmManager() = ContextCompat.getSystemService(this, AlarmManager::class.java)

fun Context.getVibrator() = ContextCompat.getSystemService(this, Vibrator::class.java)

fun Context.createKlaxonChannel(bookmark: Bookmark) {
    if (isAtLeastO()) {
        val notificationChannelGroup = NotificationChannelGroup("${bookmark.id}", bookmark.name)
        getNotificationManager().createNotificationChannelGroup(notificationChannelGroup)
        val channel = NotificationChannel(
            bookmark.klaxonChannelId(),
            getString(R.string.foreground_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            group = notificationChannelGroup.id
            setBypassDnd(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        getNotificationManager().createNotificationChannel(channel)
    }
}

fun Context.createTimerChannel(bookmark: Bookmark) {
    if (isAtLeastO()) {
        val notificationChannelGroup = NotificationChannelGroup("${bookmark.id}", bookmark.name)
        getNotificationManager().createNotificationChannelGroup(notificationChannelGroup)
        val channel = NotificationChannel(
            bookmark.runningChannelId(),
            getString(R.string.running_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { group = notificationChannelGroup.id }
        getNotificationManager().createNotificationChannel(channel)
    }
}
