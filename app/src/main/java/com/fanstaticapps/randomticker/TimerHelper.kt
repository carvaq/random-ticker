package com.fanstaticapps.randomticker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.fanstaticapps.randomticker.alarm.AlarmKlaxon
import com.fanstaticapps.randomticker.alarm.ShowNotificationWorker
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by carvaq
 * Date: 20/09/2017
 * Project: RandomTicker
 */

class TimerHelper @Inject constructor(private val notificationManager: TickerNotificationManager, private val intentHelper: IntentHelper) {
    companion object {
        const val ONE_SECOND_IN_MILLIS: Long = 1000
        private val HANDLER = Handler()
        const val WORK_TAG = "Ticker:notification"
    }

    fun createNotificationAndAlarm(context: Context) {
        val intervalFinished = PREFS.intervalFinished

        if (PREFS.showNotification) {
            notificationManager.showRunningNotification(context)

            val timerRefreshRunnable = object : Runnable {
                override fun run() {
                    if (intervalFinished <= System.currentTimeMillis() || !PREFS.currentlyTickerRunning) {
                        HANDLER.removeCallbacks(this)
                        return
                    }
                    notificationManager.showRunningNotification(context)
                    HANDLER.postDelayed(this, ONE_SECOND_IN_MILLIS)
                }
            }
            HANDLER.postDelayed(timerRefreshRunnable, ONE_SECOND_IN_MILLIS)
        }

        setAlarm(context, intervalFinished)
    }


    private fun setAlarm(context: Context, intervalFinished: Long) {
        val request = OneTimeWorkRequest.Builder(ShowNotificationWorker::class.java)
                .setInitialDelay(intervalFinished - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build()
        WorkManager.getInstance(context).enqueueUniqueWork(WORK_TAG, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancelNotificationsAndGoBack(activity: Activity) {
        AlarmKlaxon.stop()

        notificationManager.cancelNotifications(activity)

        val startIntent = intentHelper.getMainActivity(activity)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(startIntent)
        activity.overridePendingTransition(0, 0)
        activity.finish()
    }

}
