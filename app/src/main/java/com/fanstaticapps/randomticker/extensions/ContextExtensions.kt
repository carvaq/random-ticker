package com.fanstaticapps.randomticker.extensions

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark

fun Context.getNotificationManager() = NotificationManagerCompat.from(this)

fun Context.getAlarmManager() = ContextCompat.getSystemService(this, AlarmManager::class.java)

fun Context.createKlaxonChannel(bookmark: Bookmark) {
    val notificationChannelGroup = NotificationChannelGroup("${bookmark.id}", bookmark.name)
    getNotificationManager().createNotificationChannelGroup(notificationChannelGroup)
    val channel = NotificationChannel(
        bookmark.klaxonChannelId(),
        getString(R.string.foreground_channel_name),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        group = notificationChannelGroup.id
        setBypassDnd(true)
        setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        )
        enableVibration(true)
    }
    getNotificationManager().createNotificationChannel(channel)
}

fun Context.getKlaxonChannel(bookmark: Bookmark): NotificationChannel? {
    return getNotificationManager().getNotificationChannel(bookmark.klaxonChannelId())
}

fun Context.createTimerChannel(bookmark: Bookmark) {
    val notificationChannelGroup = NotificationChannelGroup("${bookmark.id}", bookmark.name)
    getNotificationManager().createNotificationChannelGroup(notificationChannelGroup)
    val channel = NotificationChannel(
        bookmark.runningChannelId(),
        getString(R.string.running_channel_name),
        NotificationManager.IMPORTANCE_LOW
    ).apply { group = notificationChannelGroup.id }
    getNotificationManager().createNotificationChannel(channel)
}
