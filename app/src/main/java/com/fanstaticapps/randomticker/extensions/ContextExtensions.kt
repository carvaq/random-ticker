package com.fanstaticapps.randomticker.extensions

import android.content.Context
import android.os.Vibrator
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun Context.getNotificationManager() = NotificationManagerCompat.from(this)


fun Context.getVibrator(): Vibrator? {
    return ContextCompat.getSystemService(this, Vibrator::class.java)
}