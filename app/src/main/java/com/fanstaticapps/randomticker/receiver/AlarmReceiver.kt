package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.data.TickerDatabase
import com.fanstaticapps.randomticker.extensions.isAppInBackground
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import dagger.android.AndroidInjection
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: TickerNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        if (context.isAppInBackground()) {
            Timber.d("Alarm received")
            val database = TickerDatabase.getInstance(context)
            database.tickerDataDao().getById(PREFS.currentSelectedId)
                    .subscribeOn(Schedulers.computation())
                    .doOnSuccess { notificationManager.showKlaxonNotification(context, it) }
                    .subscribe()
        }
    }

}
