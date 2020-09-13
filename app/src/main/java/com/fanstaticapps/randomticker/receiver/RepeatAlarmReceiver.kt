package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.TickerDatabase
import com.fanstaticapps.randomticker.helper.TimerHelper
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class RepeatAlarmReceiver : BroadcastReceiver() {

    @Inject
    internal lateinit var timerHelper: TimerHelper

    @Inject
    internal lateinit var userPreferences: UserPreferences


    override fun onReceive(context: Context, intent: Intent) {
        val database = TickerDatabase.getInstance(context)

        database.tickerDataDao().getById(userPreferences.currentSelectedId)
                .subscribeOn(Schedulers.computation())
                .doOnSuccess {
                    Timber.d("Creating a new alarm!")
                    timerHelper.newAlarmFromBookmark(context, it)
                }
                .subscribe()
    }
}
