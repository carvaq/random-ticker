package com.cvv.fanstaticapps.randomticker.helper

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.text.format.DateUtils
import com.cvv.fanstaticapps.randomticker.OnAlarmReceiver
import com.cvv.fanstaticapps.randomticker.R
import com.cvv.fanstaticapps.randomticker.activities.CancelActivity
import com.cvv.fanstaticapps.randomticker.activities.KlaxonActivityNavigator
import com.cvv.fanstaticapps.randomticker.activities.MainActivity
import javax.inject.Inject

/**
 * Created by carvaq
 * Date: 20/09/2017
 * Project: RandomTicker
 */

class TimerHelper @Inject constructor() {

    fun createNotificationAndAlarm(context: Context, tickerData: TickerData) {
        val intervalFinished = tickerData.intervalFinished
        showNotification(context, tickerData)

        val timerRefreshRunnable = object : Runnable {
            override fun run() {
                if (intervalFinished <= System.currentTimeMillis() || !tickerData.currentlyTickerRunning) {
                    HANDLER.removeCallbacks(this)
                    return
                }
                showNotification(context, tickerData)
                HANDLER.postDelayed(this, ONE_SECOND_IN_MILLIS)
            }
        }
        HANDLER.postDelayed(timerRefreshRunnable, ONE_SECOND_IN_MILLIS)

        setAlarm(context, intervalFinished)
    }

    private fun showNotification(context: Context, tickerData: TickerData) {
        val notification = buildNotification(context, tickerData)

        val notificationManager = NotificationManagerCompat.from(context)
        createNotificationChannel(context)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    private fun buildNotification(context: Context, tickerData: TickerData): Notification {
        val interval = tickerData.interval
        val intervalFinished = tickerData.intervalFinished
        val alarmPendingIntent = PendingIntent.getActivity(context, 0, KlaxonActivityNavigator(false).build(context), 0)
        val cancelPendingIntent = PendingIntent.getActivity(context, 0, Intent(context, CancelActivity::class.java), 0)
        val cancelAction = NotificationCompat.Action(R.drawable.ic_action_stop_timer,
                context.getString(android.R.string.cancel), cancelPendingIntent)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)

        val formattedInterval = getFormattedElapsedMilliseconds(interval)
        builder.addAction(cancelAction)
                .setContentTitle(context.getString(R.string.notification_title, formattedInterval))
                .setContentText(getFormattedElapsedMilliseconds(intervalFinished - System.currentTimeMillis()))
                .setAutoCancel(false)
                .setShowWhen(true)
                .setContentIntent(alarmPendingIntent)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_stat_timer)

        return builder.build()
    }

    fun getFormattedElapsedMilliseconds(elapsedMilliseconds: Long): String {
        return DateUtils.formatElapsedTime(elapsedMilliseconds / ONE_SECOND_IN_MILLIS)
    }

    private fun setAlarm(context: Context, intervalFinished: Long) {
        val pendingIntent = getAlarmPendingIntent(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, intervalFinished, pendingIntent)
    }

    private fun getAlarmPendingIntent(context: Context): PendingIntent {
        val alarmIntent = Intent(context.applicationContext, OnAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(context, REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun cancelNotificationAndGoBack(activity: Activity, preferences: TickerData) {
        cancelNotification(activity, preferences)

        val startIntent = Intent(activity, MainActivity::class.java)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(startIntent)
        activity.overridePendingTransition(0, 0)
        activity.finish()
    }

    fun cancelNotification(activity: Activity, preferences: TickerData) {
        preferences.currentlyTickerRunning = false

        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getAlarmPendingIntent(activity))

        val notificationManager = NotificationManagerCompat.from(activity)
        notificationManager.cancel(NOTIFICATION_ID)
    }


    private fun createNotificationChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = context.getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private val HANDLER = Handler()
        private const val NOTIFICATION_ID = 2312
        private const val REQUEST_CODE = 123
        private const val CHANNEL_ID = "RandomTickerChannel:01"
        const val ONE_SECOND_IN_MILLIS: Long = 1000
    }

}
