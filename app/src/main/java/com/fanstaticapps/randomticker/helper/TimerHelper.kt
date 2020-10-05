package com.fanstaticapps.randomticker.helper

import android.app.Activity
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.content.Context
import android.os.Build
import android.os.Handler
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
                                      private val userPreferences: UserPreferences) {
    companion object {
        const val ONE_SECOND_IN_MILLIS: Long = 1000
        private val HANDLER = Handler()
    }

    private val randomGenerator = Random(System.currentTimeMillis())

    fun newAlarmFromBookmark(context: Context, bookmark: Bookmark) {
        Timber.d("creating a new ticker for bookmark $bookmark")
        cancelTicker(context)
        createTicker(bookmark.getMinimumIntervalDefinition(), bookmark.getMaximumIntervalDefinition())
        startTicker(context)
    }

    fun createTicker(minIntervalDefinition: IntervalDefinition, maxIntervalDefinition: IntervalDefinition): Boolean {
        val min = minIntervalDefinition.millis
        val max = maxIntervalDefinition.millis
        Timber.d("Creating a ticker between $min and $max")
        return if (max > min) {
            val interval = (randomGenerator.nextInt(max - min + 1) + min).toLong()
            userPreferences.setTickerInterval(interval)
            true
        } else {
            false
        }
    }

    fun startTicker(context: Context) {
        val intervalFinished = userPreferences.intervalWillBeFinished

        if (isTickerInvalid()) return


        if (userPreferences.showRunningTimerNotification) {
            Timber.d("showing running ticker notification")

            notificationManager.showRunningNotification(context)

            val tickerRefreshRunnable = object : Runnable {
                override fun run() {
                    if (isTickerInvalid()) {
                        HANDLER.removeCallbacks(this)
                        return
                    }
                    notificationManager.showRunningNotification(context)
                    HANDLER.postDelayed(this, ONE_SECOND_IN_MILLIS)
                }
            }
            HANDLER.postDelayed(tickerRefreshRunnable, ONE_SECOND_IN_MILLIS)
        }

        context.getAlarmManager()?.let { alarmManger ->
            val alarmIntent = IntentHelper.getAlarmReceiveAsPendingIntent(context)
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManger.setAlarmClock(AlarmClockInfo(intervalFinished, alarmIntent), alarmIntent)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManger.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, intervalFinished, alarmIntent)
                }
                else -> {
                    alarmManger.setExact(AlarmManager.RTC_WAKEUP, intervalFinished, alarmIntent)

                }
            }
            Timber.d("Setting alarm to sound in ${(intervalFinished - System.currentTimeMillis()) / 1000}s")
        }
    }

    fun isCurrentlyTickerRunning(): Boolean {
        val intervalWillBeFinished = userPreferences.intervalWillBeFinished
        val tickerRunning = intervalWillBeFinished > 0
        Timber.d("Currently is a ticker running: $tickerRunning ($intervalWillBeFinished)")
        return tickerRunning
    }

    fun isTickerInvalid(): Boolean {
        val intervalWillBeFinished = userPreferences.intervalWillBeFinished
        val tickerRunning = intervalWillBeFinished > 0 && intervalWillBeFinished >= System.currentTimeMillis()
        Timber.d("Ticker is valid: $tickerRunning")
        return !tickerRunning
    }

    fun cancelTicker(context: Context) {
        Timber.d("Cancel ticker")
        notificationManager.cancelAllNotifications(context)

        userPreferences.resetInterval()

        val alarmIntent = IntentHelper.getAlarmReceiveAsPendingIntent(context)
        context.getAlarmManager()?.cancel(alarmIntent)
    }

    fun startNotification(activity: Activity, bookmark: Bookmark) {
        notificationManager.showKlaxonNotification(activity, bookmark)
    }

}
