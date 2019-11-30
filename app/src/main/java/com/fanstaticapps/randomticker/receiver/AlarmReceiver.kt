package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.data.TickerDatabase
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: TickerNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        Timber.d("Alarm received")
        val database = TickerDatabase.getInstance(context)
        database.tickerDataDao().getById(PREFS.currentSelectedId)
                .doOnSuccess { notificationManager.showKlaxonNotification(context, it) }
                .subscribe()
    }
}
