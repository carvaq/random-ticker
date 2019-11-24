package com.fanstaticapps.randomticker.helper

import android.content.Context
import android.os.PowerManager
import androidx.core.content.ContextCompat

object AlarmAlertWakeLock {
    private const val WAKE_LOCK_TAG = "RandomTicker:AlarmAlertWakeLock"

    private var cpuWakeLock: PowerManager.WakeLock? = null

    fun createPartialWakeLock(context: Context): PowerManager.WakeLock? {
        val pm = ContextCompat.getSystemService(context, PowerManager::class.java)
        return pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG)
    }

    fun acquireCpuWakeLock(context: Context) {
        if (cpuWakeLock != null) {
            return
        }

        cpuWakeLock = createPartialWakeLock(context)
        cpuWakeLock?.acquire(10 * 60 * 1000L /*10 minutes*/)
    }

    fun acquireScreenCpuWakeLock(context: Context) {
        if (cpuWakeLock != null) {
            return
        }
        val pm = ContextCompat.getSystemService(context, PowerManager::class.java)
        cpuWakeLock = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, WAKE_LOCK_TAG)
        cpuWakeLock?.acquire(10 * 60 * 1000L /*10 minutes*/)
    }

    fun releaseCpuLock() {
        cpuWakeLock?.release()
        cpuWakeLock = null
    }
}