package com.fanstaticapps.randomticker.helper

import android.app.AlarmManager.RTC_WAKEUP
import android.content.Context
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.getAlarmManager
import com.fanstaticapps.randomticker.extensions.isAtLeastS
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject


class TimerHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: TickerNotificationManager,
) {
    companion object {
        const val ONE_SECOND_IN_MILLIS: Long = 1000
    }

    fun startTicker(bookmark: Bookmark) {
        Timber.d("creating a new ticker for bookmark $bookmark")
        cancelTicker(bookmark.id)

        Timber.d("showing running ticker notification")
        notificationManager.showRunningNotification(context, bookmark)

        val alarmManger = context.getAlarmManager()
        if (!isAtLeastS() || alarmManger?.canScheduleExactAlarms() == true) {
            val alarmIntent = IntentHelper.getAlarmReceiverPendingIntent(context, bookmark.id)
            alarmManger?.setExactAndAllowWhileIdle(RTC_WAKEUP, bookmark.intervalEnd, alarmIntent)
            Timber.d("Setting alarm to sound in ${(bookmark.intervalEnd - System.currentTimeMillis()) / 1000}s")
        }
    }

    fun cancelTicker(bookmarkId: Long?) {
        Timber.d("Cancel ticker")
        notificationManager.cancelAllNotifications(context)
        context.getAlarmManager()
            ?.cancel(IntentHelper.getCancelAlarmReceiverAsPendingIntent(context, bookmarkId))
    }
}