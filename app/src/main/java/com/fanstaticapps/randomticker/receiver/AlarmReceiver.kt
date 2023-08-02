package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var tickerNotificationManager: TickerNotificationManager
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Alarm received")

        tickerNotificationManager.showNotificationWithFullScreenIntent(context)
        //IntentHelper.getRingtoneServiceIntent(context).let { context.startService(it) }
    }


}
