package com.fanstaticapps.randomticker.helper

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.os.Handler
import androidx.core.app.AlarmManagerCompat
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.IntervalDefinition
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
        createTimer(bookmark.getMinimumIntervalDefinition(), bookmark.getMaximumIntervalDefinition())
        createAlarm(context)
    }

    fun createTimer(minIntervalDefinition: IntervalDefinition, maxIntervalDefinition: IntervalDefinition): Boolean {
        val min = minIntervalDefinition.millis
        val max = maxIntervalDefinition.millis
        return if (max > min) {
            val interval = (randomGenerator.nextInt(max - min + 1) + min).toLong()
            userPreferences.setTickerInterval(interval)
            true
        } else {
            false
        }
    }


    fun createAlarm(context: Context) {
        if (!isCurrentlyTickerRunning()) return

        val intervalFinished = userPreferences.intervalFinished

        if (userPreferences.showNotification) {
            notificationManager.showRunningNotification(context)

            val timerRefreshRunnable = object : Runnable {
                override fun run() {
                    if (!isCurrentlyTickerRunning()) {
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

    fun isCurrentlyTickerRunning(): Boolean {
        return userPreferences.intervalFinished <= System.currentTimeMillis() && userPreferences.intervalFinished > 0
    }


    private fun setAlarm(context: Context, intervalFinished: Long) {
        context.getAlarmManager()?.let {
            val alarmIntent = intentHelper.getAlarmReceiveAsPendingIntent(context)
            AlarmManagerCompat.setAndAllowWhileIdle(it, AlarmManager.RTC_WAKEUP, intervalFinished, alarmIntent)
            Timber.d("Setting alarm to sound in ${(intervalFinished - System.currentTimeMillis()) / 1000}s")
        }
    }

    fun cancelTimerForNewConfiguration(activity: Activity) {
        cancelAlarm(activity)

        notificationManager.cancelNotifications(activity)

        userPreferences.resetInterval()

        val startIntent = intentHelper.getMainActivity(activity)
        activity.startActivity(startIntent)
        activity.overridePendingTransition(0, 0)
        activity.finish()
    }

    private fun cancelAlarm(context: Context) {
        Timber.d("Cancel timer")
        context.getAlarmManager()?.let {
            val alarmIntent = intentHelper.getAlarmReceiveAsPendingIntent(context)
            it.cancel(alarmIntent)
        }
    }

    fun startNotification(activity: Activity, bookmark: Bookmark) {
        notificationManager.showKlaxonNotification(activity, bookmark)
    }

}
