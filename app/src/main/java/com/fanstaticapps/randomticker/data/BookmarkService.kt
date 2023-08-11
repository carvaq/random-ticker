package com.fanstaticapps.randomticker.data

import com.fanstaticapps.randomticker.helper.AlarmCoordinator
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
    private val notificationCoordinator: NotificationCoordinator,
    private val alarmCoordinator: AlarmCoordinator,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {

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

    suspend fun createNew(): Long {
        return withContext(coroutineScope.coroutineContext) {
            var newBookmark = Bookmark()
            val id = repository.insertOrUpdateBookmark(newBookmark)
            newBookmark = newBookmark.copy(id = id)
            notificationCoordinator.triggerNotificationChannelNotification(newBookmark)
            id
        }
    }

    fun intervalEnded(bookmarkId: Long) {
        fetchBookmarkById(bookmarkId) {
            notificationCoordinator.cancelAllNotifications(it)
            notificationCoordinator.showKlaxonNotification(it)
            if (it.autoRepeat) scheduleAlarm(bookmarkId, false)
        }
    }

    fun scheduleAlarm(bookmarkId: Long, isManuallyTriggered: Boolean) {
        fetchBookmarkById(bookmarkId) {
            if (!isManuallyTriggered && it.autoRepeat) delay(it.autoRepeatInterval)
            val bookmark = it.saveBookmarkWithNewInterval()
            Timber.d("creating a new ticker for bookmark $bookmark")
            notificationCoordinator.cancelAllNotifications(bookmark)

            Timber.d("showing running ticker notification")
            notificationCoordinator.showRunningNotification(bookmark)
            alarmCoordinator.scheduleAlarm(bookmark)
        }
    }

    private fun Bookmark.saveBookmarkWithNewInterval(): Bookmark {
        val interval = randomGenerator.nextInt((max - min + 1).toInt()) + min.millis
        return copy(intervalEnd = interval + System.currentTimeMillis())
            .also { repository.insertOrUpdateBookmark(it) }
    }

    fun cancelTicker(bookmarkId: Long) {
        cancelTicker(bookmarkId) {}
    }

    fun delete(bookmark: Bookmark) {
        cancelTicker(bookmark.id) {
            notificationCoordinator.deleteChannelsForBookmark(bookmark)
            repository.deleteBookmark(bookmark)
        }
    }

    private fun cancelTicker(
        bookmarkId: Long,
        afterCancelling: suspend () -> Unit
    ) {
        fetchBookmarkById(bookmarkId) {
            Timber.d("cancel bookmark $it")
            repository.insertOrUpdateBookmark(it.reset())
            notificationCoordinator.cancelAllNotifications(it)
            alarmCoordinator.cancelAlarm(it)
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


}