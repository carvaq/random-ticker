package com.fanstaticapps.randomticker.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.EXTRA_BOOKMARK_ID
import com.fanstaticapps.randomticker.extensions.getAlarmManager
import com.fanstaticapps.randomticker.extensions.isAtLeastS
import com.fanstaticapps.randomticker.receiver.AlarmEndedReceiver
import timber.log.Timber

class AlarmCoordinator(private val context: Context) {
    fun scheduleAlarm(bookmark: Bookmark) {
        val alarmManger = context.getAlarmManager()
        if (!isAtLeastS() || alarmManger?.canScheduleExactAlarms() == true) {
            val alarmIntent = context.getAlarmEndedReceiverPendingIntent(
                PendingIntent.FLAG_UPDATE_CURRENT,
                bookmark
            )
            alarmManger?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                bookmark.intervalEnd,
                alarmIntent
            )
            Timber.d("Setting alarm to sound in ${(bookmark.intervalEnd - System.currentTimeMillis()) / 1000}s")
        }
    }

    fun cancelAlarm(bookmark: Bookmark) {
        context.getAlarmManager()?.cancel(
            context.getAlarmEndedReceiverPendingIntent(
                PendingIntent.FLAG_CANCEL_CURRENT,
                bookmark
            )
        )
    }

    private fun Context.getAlarmEndedReceiverPendingIntent(
        flag: Int,
        bookmark: Bookmark
    ): PendingIntent? {
        val intent = Intent(AlarmEndedReceiver.ACTION)
        intent.component = ComponentName(this, AlarmEndedReceiver::class.java)
        intent.putExtra(EXTRA_BOOKMARK_ID, bookmark.id)
        return PendingIntent.getBroadcast(
            this,
            bookmark.klaxonNotificationId,
            intent,
            flag or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
