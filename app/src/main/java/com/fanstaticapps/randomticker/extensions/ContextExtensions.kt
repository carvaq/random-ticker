package com.fanstaticapps.randomticker.extensions

import android.app.ActivityManager
import android.app.AlarmManager
import android.content.Context
import android.os.Vibrator
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun Context.getNotificationManager() = NotificationManagerCompat.from(this)

fun Context.getAlarmManager() = ContextCompat.getSystemService(this, AlarmManager::class.java)

fun Context.isAppInBackground(): Boolean {
    val am = ContextCompat.getSystemService(this, ActivityManager::class.java)

    return am?.runningAppProcesses?.find { processInfo ->
        processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }?.pkgList?.find { activeProcess -> activeProcess == packageName } == null
}

fun Context.getVibrator(): Vibrator? {
    return ContextCompat.getSystemService(this, Vibrator::class.java)
}