package com.fanstaticapps.randomticker.helper

import android.app.Activity
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.IntervalDefinition
import com.fanstaticapps.randomticker.extensions.getAlarmManager
import timber.log.Timber
import java.util.Random
import javax.inject.Inject


/**
 * Created by carvaq
 * Date: 20/09/2017
 * Project: RandomTicker
 */

class TimerHelper @Inject constructor(
    private val notificationManager: TickerNotificationManager,
    private val tickerPreferences: TickerPreferences
) {
    companion object {
        const val ONE_SECOND_IN_MILLIS: Long = 1000
        private val HANDLER = Handler(Looper.myLooper() ?: Looper.getMainLooper())
    }

    private val randomGenerator = Random(System.currentTimeMillis())

    fun newTickerFromBookmark(context: Context, bookmark: Bookmark) {
        Timber.d("creating a new ticker for bookmark $bookmark")
        cancelTicker(context)
        createTicker(
            bookmark.getMinimumIntervalDefinition(),
            bookmark.getMaximumIntervalDefinition()
        )
        startTicker(context)
    }

    fun createTicker(
        minIntervalDefinition: IntervalDefinition,
        maxIntervalDefinition: IntervalDefinition
    ): Boolean {
        val min = minIntervalDefinition.millis
        val max = maxIntervalDefinition.millis
        Timber.d("Creating a ticker between $min and $max")
        return if (max > min) {
            val interval = (randomGenerator.nextInt(max - min + 1) + min).toLong()
            tickerPreferences.setTickerInterval(interval)
            true
        } else {
            false
        }
    }

    fun startTicker(context: Context) {
        val intervalFinished = tickerPreferences.intervalWillBeFinished

        if (isTickerInvalid()) return


        if (tickerPreferences.showRunningTimerNotification) {
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
            val alarmIntent = IntentHelper.getAlarmReceiveAsPendingIntent(
                context,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManger.setAlarmClock(
                        AlarmClockInfo(intervalFinished, alarmIntent),
                        alarmIntent
                    )
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManger.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        intervalFinished,
                        alarmIntent
                    )
                }
                else -> {
                    alarmManger.setExact(AlarmManager.RTC_WAKEUP, intervalFinished, alarmIntent)

                }
            }
            Timber.d("Setting alarm to sound in ${(intervalFinished - System.currentTimeMillis()) / 1000}s")
        }
    }

    fun isCurrentlyTickerRunning(): Boolean {
        val intervalWillBeFinished = tickerPreferences.intervalWillBeFinished
        val tickerRunning = intervalWillBeFinished > 0
        Timber.d("Currently is a ticker running: $tickerRunning ($intervalWillBeFinished)")
        return tickerRunning
    }

    fun isTickerInvalid(): Boolean {
        val intervalWillBeFinished = tickerPreferences.intervalWillBeFinished
        val hasTickerNotExpiredYet = intervalWillBeFinished >= System.currentTimeMillis()
        Timber.d("Ticker is valid: $hasTickerNotExpiredYet")
        return !hasTickerNotExpiredYet
    }

    fun cancelTicker(context: Context) {
        Timber.d("Cancel ticker")
        notificationManager.cancelAllNotifications(context)

        tickerPreferences.resetInterval()

        IntentHelper.getAlarmReceiveAsPendingIntent(context, PendingIntent.FLAG_NO_CREATE)
            ?.let { alarmIntent ->
                context.getAlarmManager()?.cancel(alarmIntent)
            }
    }

    fun startNotification(activity: Activity, bookmark: Bookmark) {
        notificationManager.showKlaxonNotification(activity, bookmark)
    }

}
