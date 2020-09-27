package com.fanstaticapps.randomticker.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.provider.AlarmClock
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
        if (max > min) {
            val interval = (randomGenerator.nextInt(max - min + 1) + min).toLong()
            val intervalFinished = System.currentTimeMillis() + interval
            userPreferences.currentlyTickerRunning = true
            userPreferences.interval = interval
            userPreferences.intervalFinished = intervalFinished
        } else {
            userPreferences.currentlyTickerRunning = false
        }
        return userPreferences.currentlyTickerRunning
    }


    fun createAlarm(context: Context) {
        if (!userPreferences.currentlyTickerRunning) return

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
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, intervalFinished)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Random Ticker")
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        context.startActivity(intent)


        context.getAlarmManager()?.let {
            val alarmIntent = intentHelper.getAlarmReceiveAsPendingIntent(context)
            // AlarmManagerCompat.setAndAllowWhileIdle(it, AlarmManager.RTC_WAKEUP, intervalFinished, alarmIntent)
            Timber.d("Setting alarm to sound in ${(intervalFinished - System.currentTimeMillis()) / 1000}s")
        }
    }

    fun cancelNotificationsAndGoBack(activity: Activity) {
        AlarmKlaxon.stop(activity)
        cancelAlarm(activity)

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
