package com.fanstaticapps.randomticker.extensions

import android.content.Context
import android.os.PowerManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


fun Context.isScreenOn(): Boolean {
    val powerManager = ContextCompat.getSystemService(this, PowerManager::class.java)
    return powerManager?.isInteractive ?: false
}


fun Context.getNotificationManager() = NotificationManagerCompat.from(this)
