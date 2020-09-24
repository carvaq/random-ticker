package com.fanstaticapps.randomticker.helper

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.core.app.AlarmManagerCompat
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.getAlarmManager
import timber.log.Timber
import java.util.*
import javax.inject.Inject


/**
 * Created by carvaq
 * Date: 20/09/2017
 * Project: RandomTicker
 */

class TimerHelper @Inject constructor(private val notificationManager: TickerNotificationManager,
                                      private val intentHelper: IntentHelper,
                                      private val userPreferences: UserPreferences) {
    companion object {
        const val ONE_SECOND_IN_MILLIS: Long = 1000
        private val HANDLER = Handler()
    }


    private val randomGenerator = Random(System.currentTimeMillis())

    fun newAlarmFromBookmark(context: Context, bookmark: Bookmark) {
        notificationManager.cancelNotifications(context)
        createTimer(bookmark.minimumMinutes, bookmark.minimumSeconds, bookmark.maximumMinutes, bookmark.maximumSeconds)
        createAlarm(context)
    }

    fun createTimer(minMin: Int, minSec: Int, maxMin: Int, maxSec: Int): Boolean {
        val min = getTotalValueInMillis(minMin, minSec)
        val max = getTotalValueInMillis(maxMin, maxSec)
        return if (max > min) {
            val interval = (randomGenerator.nextInt(max - min + 1) + min).toLong()
            val intervalFinished = System.currentTimeMillis() + interval
            saveToPreferences(interval, intervalFinished)
            true
        } else {
            false
        }
    }


    private fun getTotalValueInMillis(minutes: Int, seconds: Int): Int {
        return (60 * minutes + seconds) * 1000
    }

    private fun saveToPreferences(interval: Long, intervalFinished: Long) {
        userPreferences.currentlyTickerRunning = true
        userPreferences.interval = interval
        userPreferences.intervalFinished = intervalFinished
    }


    fun createAlarm(context: Context) {
        val intervalFinished = userPreferences.intervalFinished

        if (userPreferences.showNotification) {
            notificationManager.showRunningNotification(context)

            val timerRefreshRunnable = object : Runnable {
                override fun run() {
                    if (intervalFinished <= System.currentTimeMillis() || !userPreferences.currentlyTickerRunning) {
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
        context.getAlarmManager()?.let {
            val alarmIntent = intentHelper.getAlarmReceiveAsPendingIntent(context)
            AlarmManagerCompat.setAndAllowWhileIdle(it, AlarmManager.RTC_WAKEUP, intervalFinished, alarmIntent)
            Timber.d("Setting alarm to sound in ${(intervalFinished - System.currentTimeMillis()) / 1000}s")
        }
    }

    fun cancelNotificationsAndGoBack(activity: Activity) {
        AlarmKlaxon.stop(activity)

        notificationManager.cancelNotifications(activity)

        val startIntent = intentHelper.getMainActivity(activity)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(startIntent)
        activity.overridePendingTransition(0, 0)
        activity.finish()
    }

    fun cancelAlarm(context: Context) {
        Timber.d("Cancel timer")
        context.getAlarmManager()?.let {
            val alarmIntent = intentHelper.getAlarmReceiveAsPendingIntent(context)
            it.cancel(alarmIntent)
        }
    }

    fun startNotification(activity: Activity) {
        AlarmKlaxon.start(activity, userPreferences)
        notificationManager.cancelNotifications(activity)
    }

}
