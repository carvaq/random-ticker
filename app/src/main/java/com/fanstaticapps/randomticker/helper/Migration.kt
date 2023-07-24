package com.fanstaticapps.randomticker.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.extensions.getNotificationManager
import com.fanstaticapps.randomticker.extensions.isAtLeastO
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class Migration @Inject constructor(
    private val tickerPreferences: TickerPreferences,
    @ActivityContext private val context: Context
) {


    fun migrate() {
        if (isAtLeastO()) {
            val notificationManager = context.getNotificationManager()
            notificationManager.getNotificationChannel(OLD_RUNNING_CHANNEL_ID)?.let {
                NotificationChannel(
                    TickerNotificationManager.RUNNING_CHANNEL_ID,
                    it.name,
                    NotificationManager.IMPORTANCE_HIGH
                )
            }
        }
    }

    companion object {
        const val OLD_RUNNING_CHANNEL_ID = "RandomTickerChannel:01"

    }
}
