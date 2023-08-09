package com.fanstaticapps.randomticker.data

import android.app.AlarmManager
import android.content.Context
import com.fanstaticapps.randomticker.extensions.getAlarmManager
import com.fanstaticapps.randomticker.extensions.isAtLeastS
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.NotificationCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Random

class BookmarkService(
    private val repository: BookmarkRepository,
) {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val randomGenerator = Random()

    fun getBookmarkById(bookmarkId: Long): Flow<Bookmark> {
        return repository.getBookmarkById(bookmarkId)
            .flowOn(Dispatchers.IO)
            .filterNotNull()
    }

    fun save(bookmark: Bookmark) {
        coroutineScope.launch {
            repository.insertOrUpdateBookmark(bookmark)
        }
    }

    suspend fun createNew(context: Context): Long {
        return withContext(coroutineScope.coroutineContext) {
            var newBookmark = Bookmark()
            val id = repository.insertOrUpdateBookmark(newBookmark)
            newBookmark = newBookmark.copy(id = id)
            NotificationCoordinator.triggerNotificationChannelNotification(context, newBookmark)
            id
        }
    }

    fun intervalEnded(context: Context, bookmarkId: Long) {
        fetchBookmarkById(bookmarkId) {
            NotificationCoordinator.showNotificationWithFullScreenIntent(
                context,
                it
            )
        }
    }

    fun scheduleAlarm(context: Context, bookmarkId: Long, isManuallyTriggered: Boolean) {
        fetchBookmarkById(bookmarkId) {
            if (!isManuallyTriggered && it.autoRepeat) delay(it.autoRepeatInterval)
            val bookmark = it.saveBookmarkWithNewInterval()
            Timber.d("creating a new ticker for bookmark $bookmarkId")
            NotificationCoordinator.cancelAllNotifications(context, bookmark)

            Timber.d("showing running ticker notification")
            NotificationCoordinator.showRunningNotification(context, bookmark)

            val alarmManger = context.getAlarmManager()
            if (!isAtLeastS() || alarmManger?.canScheduleExactAlarms() == true) {
                val alarmIntent =
                    IntentHelper.getAlarmEndedReceiverPendingIntent(context, bookmarkId)
                alarmManger?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    bookmark.intervalEnd,
                    alarmIntent
                )
                Timber.d("Setting alarm to sound in ${(bookmark.intervalEnd - System.currentTimeMillis()) / 1000}s")
            }
        }
    }

    private fun Bookmark.saveBookmarkWithNewInterval(): Bookmark {
        val interval = randomGenerator.nextInt((max - min + 1).toInt()) + min.millis
        return copy(intervalEnd = interval + System.currentTimeMillis())
            .also { repository.insertOrUpdateBookmark(it) }
    }

    fun cancelTicker(context: Context, bookmarkId: Long) {
        cancelTicker(context, bookmarkId) {}
    }

    private fun cancelTicker(
        context: Context,
        bookmarkId: Long,
        afterCancelling: suspend () -> Unit
    ) {
        fetchBookmarkById(bookmarkId) {
            Timber.d("cancel bookmark $it")
            repository.insertOrUpdateBookmark(it.reset())
            NotificationCoordinator.cancelAllNotifications(context, it)
            context.getAlarmManager()
                ?.cancel(
                    IntentHelper.getAlarmEndedReceiverCancelPendingIntent(
                        context,
                        bookmarkId
                    )
                )
            afterCancelling()
        }
    }

    private fun fetchBookmarkById(bookmarkId: Long, onBookmark: suspend (Bookmark) -> Unit) {
        coroutineScope.launch {
            getBookmarkById(bookmarkId).take(1).collect(onBookmark)
        }
    }

    fun applyForAllBookmarks(action: (Bookmark) -> Unit) {
        coroutineScope.launch {
            repository.getAllBookmarks().firstOrNull()?.forEach(action)
        }
    }

    fun fetchAllBookmarks() = repository.getAllBookmarks()

    fun delete(context: Context, bookmark: Bookmark) {
        cancelTicker(context, bookmark.id) {
            repository.deleteBookmark(bookmark)
        }
    }
}