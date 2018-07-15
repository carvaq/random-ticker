package com.cvv.fanstaticapps.randomticker.helper

import android.content.Context
import android.os.PowerManager

object WakeLocker {

    private var wake: PowerManager.WakeLock? = null

    fun acquireLock(context: Context) {
        wake != null ?: return
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wake = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                or PowerManager.ACQUIRE_CAUSES_WAKEUP, "App:wakeuptag")
        wake?.acquire(10 * 60 * 1000L /*10 minutes*/)
    }

    fun release() {
        if (wake != null && wake!!.isHeld) {
            wake!!.release()
        }
    }
}