package com.fanstaticapps.randomticker.helper

import android.app.AlarmManager
import android.content.Context
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.getAlarmManager
import com.fanstaticapps.randomticker.extensions.isAtLeastS
import timber.log.Timber

class AlarmCoordinator(private val context: Context) {
    fun scheduleAlarm(bookmark: Bookmark) {
        val alarmManger = context.getAlarmManager()
        if (!isAtLeastS() || alarmManger?.canScheduleExactAlarms() == true) {
            val alarmIntent =
                IntentHelper.getAlarmEndedReceiverPendingIntent(context, bookmark.id)
            alarmManger?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                bookmark.intervalEnd,
                alarmIntent
            )
            Timber.d("Setting alarm to sound in ${(bookmark.intervalEnd - System.currentTimeMillis()) / 1000}s")
        }
    }

    fun cancelAlarm(bookmarkId: Long) {
        context.getAlarmManager()
            ?.cancel(
                IntentHelper.getAlarmEndedReceiverCancelPendingIntent(
                    context,
                    bookmarkId
                )
            )
    }

}
