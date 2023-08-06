package com.fanstaticapps.randomticker.data

import android.app.AlarmManager
import android.content.Context
import com.fanstaticapps.randomticker.extensions.getAlarmManager
import com.fanstaticapps.randomticker.extensions.isAtLeastS
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class BookmarkService @Inject constructor(
    private val repository: BookmarkRepository,
    private val notificationManager: TickerNotificationManager,
) {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun getBookmarkById(bookmarkId: Long): Flow<Bookmark> {
        return repository.getBookmarkById(bookmarkId)
            .flowOn(Dispatchers.IO)
            .filterNotNull()
    }

    fun createOrUpdate(context: Context, bookmark: Bookmark) {
        coroutineScope.launch {
            val id = repository.insertOrUpdateBookmark(bookmark)
            schedulerAlarm(context, id, true)
        }
    }

    fun intervalEnded(context: Context, bookmarkId: Long) {
        fetchBookmarkById(bookmarkId) {
            notificationManager.showNotificationWithFullScreenIntent(
                context,
                it
            )
        }
    }

    fun schedulerAlarm(context: Context, bookmarkId: Long, isManuallyTriggered: Boolean) {
        fetchBookmarkById(bookmarkId) {
            if (isManuallyTriggered || !it.autoRepeat) {
                Timber.d("creating a new ticker for bookmark $bookmarkId")
                notificationManager.cancelAllNotifications(context, it)

                Timber.d("showing running ticker notification")
                notificationManager.showRunningNotification(context, it)

                val alarmManger = context.getAlarmManager()
                if (!isAtLeastS() || alarmManger?.canScheduleExactAlarms() == true) {
                    val alarmIntent =
                        IntentHelper.getAlarmEndedReceiverPendingIntent(context, bookmarkId)
                    alarmManger?.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        it.intervalEnd,
                        alarmIntent
                    )
                    Timber.d("Setting alarm to sound in ${(it.intervalEnd - System.currentTimeMillis()) / 1000}s")
                }
            }
        }
    }

    fun cancelTicker(context: Context, bookmarkId: Long) {
        fetchBookmarkById(bookmarkId) {
            Timber.d("cancel bookmark $it")
            repository.insertOrUpdateBookmark(it.reset())
            notificationManager.cancelAllNotifications(context, it)
            context.getAlarmManager()
                ?.cancel(
                    IntentHelper.getAlarmEndedReceiverCancelPendingIntent(
                        context,
                        bookmarkId
                    )
                )
        }
    }

    private fun fetchBookmarkById(bookmarkId: Long, onBookmark: suspend (Bookmark) -> Unit) {
        coroutineScope.launch {
            getBookmarkById(bookmarkId).take(1).collect(onBookmark)
        }
    }
}