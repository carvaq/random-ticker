package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.TickerDatabase
import com.fanstaticapps.randomticker.extensions.isAppInBackground
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: TickerNotificationManager

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onReceive(context: Context, intent: Intent) {

        if (context.isAppInBackground()) {
            Timber.d("Alarm received")
            val database = TickerDatabase.getInstance(context)
            database.tickerDataDao().getById(userPreferences.currentSelectedId)
                    .subscribeOn(Schedulers.computation())
                    .doOnSuccess { notificationManager.showKlaxonNotification(context, it) }
                    .subscribe()
        }
    }

}
