package com.fanstaticapps.randomticker.alarm

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.helper.AlarmAlertWakeLock
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import dagger.android.AndroidInjection
import javax.inject.Inject

class AlarmService : Service() {

    @Inject
    lateinit var notificationManager: TickerNotificationManager

    private val binder = Binder()

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_NOT_STICKY
        }

        startAlarm()

        return START_NOT_STICKY
    }


    private fun startAlarm() {
        PREFS.currentlyTickerRunning = false

        AlarmAlertWakeLock.acquireCpuWakeLock(this)

        notificationManager.showKlaxonNotification(applicationContext)

        AlarmKlaxon.start(this)
    }

}
