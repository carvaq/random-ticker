package com.fanstaticapps.randomticker.extensions

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.fanstaticapps.randomticker.data.Bookmark

fun Context.getNotificationManager() = NotificationManagerCompat.from(this)

fun Context.getAlarmManager() = ContextCompat.getSystemService(this, AlarmManager::class.java)

fun Context.createNotificationChannel(
    bookmark: Bookmark,
    soundUri: Uri? = bookmark.soundUri?.toUri(),
    enableVibration: Boolean = true
): NotificationChannel? {
    val channel = NotificationChannel(
        bookmark.notificationChannelId,
        RingtoneManager.getRingtone(this, soundUri).getTitle(this),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        setSound(
            soundUri,
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        )
        enableVibration(enableVibration)
    }
    getNotificationManager().createNotificationChannel(channel)
    return getNotificationManager().getNotificationChannel(bookmark.notificationChannelId)
}

fun Context.deleteChannel(bookmark: Bookmark) {
    getNotificationManager().deleteNotificationChannel(bookmark.notificationChannelId)
}

fun Context.needsPostNotificationPermission(): Boolean {
    return isAtLeastT() && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
    ) != PackageManager.PERMISSION_GRANTED
}

fun Context.needsScheduleAlarmPermission(): Boolean {
    return isAtLeastS() && getAlarmManager()?.canScheduleExactAlarms() != true
}