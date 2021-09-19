package com.fanstaticapps.randomticker.extensions

import android.app.AlarmManager
import android.content.Context
import android.os.Vibrator
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun Context.getNotificationManager() = NotificationManagerCompat.from(this)

fun Context.getAlarmManager() = ContextCompat.getSystemService(this, AlarmManager::class.java)

fun Context.getVibrator() = ContextCompat.getSystemService(this, Vibrator::class.java)
