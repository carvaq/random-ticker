package com.fanstaticapps.randomticker.extensions

import android.app.Activity
import android.app.KeyguardManager
import android.view.WindowManager
import androidx.core.content.ContextCompat

fun Activity.turnScreenOnAndKeyguardOff() {
    if (isAtLeastO_MR1()) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }
    ContextCompat.getSystemService(this, KeyguardManager::class.java)
        ?.requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
}

fun Activity.turnScreenOffAndKeyguardOn() {
    if (isAtLeastO_MR1()) {
        setShowWhenLocked(false)
        setTurnScreenOn(false)
    } else {
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }
}