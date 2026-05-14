package com.fanstaticapps.randomticker.data

import com.fanstaticapps.randomticker.helper.AlarmCoordinator
import com.fanstaticapps.randomticker.helper.NotificationCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

class BookmarkService(
    private val repository: BookmarkRepository,
    private val notificationCoordinator: NotificationCoordinator,
    private val alarmCoordinator: AlarmCoordinator,
    private val coroutineScope: CoroutineScope,
) {
    constructor(
        repository: BookmarkRepository,
        notificationCoordinator: NotificationCoordinator,
        alarmCoordinator: AlarmCoordinator,
    ) : this(
        repository,
        notificationCoordinator,
        alarmCoordinator,
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    )

    fun getBookmarkById(bookmarkId: Long): Flow<Bookmark> = repository.getBookmarkById(bookmarkId)
        .flowOn(Dispatchers.IO)
        .filterNotNull()

    fun save(bookmark: Bookmark) = coroutineScope.launch {
        repository.insertOrUpdateBookmark(bookmark)
    }

    suspend fun createNew(): Long {
        val newBookmark = Bookmark()
        val id = repository.insertOrUpdateBookmark(newBookmark)
        notificationCoordinator.triggerNotificationChannelNotification(newBookmark.copy(id = id))
        return id
    }

    fun intervalEnded(bookmarkId: Long): Job = coroutineScope.launch {
        repository.getBookmarkByIdOnce(bookmarkId)?.let {
            notificationCoordinator.cancelAllNotifications(it)
            notificationCoordinator.showKlaxonNotification(it)
            if (it.autoRepeat) scheduleAlarm(bookmarkId, false).join()
        }
    }

    fun scheduleAlarm(bookmarkId: Long, isManuallyTriggered: Boolean): Job = coroutineScope.launch {
        repository.getBookmarkByIdOnce(bookmarkId)?.let {
            if (!isManuallyTriggered && it.autoRepeat) delay(it.autoRepeatInterval)
            val bookmark = it.saveBookmarkWithNewInterval()
            Timber.d("creating a new ticker for bookmark $bookmark")
            notificationCoordinator.cancelAllNotifications(bookmark)

            Timber.d("showing running ticker notification")
            notificationCoordinator.showRunningNotification(bookmark)
            alarmCoordinator.scheduleAlarm(bookmark)
        }
    }

    fun cancel(bookmarkId: Long): Job = coroutineScope.launch {
        cancelTimer(bookmarkId)
    }

    fun delete(bookmarkId: Long): Job = coroutineScope.launch {
        cancelTimer(bookmarkId)?.let { bookmark ->
            notificationCoordinator.deleteChannelsForBookmark(bookmark)
            repository.deleteBookmark(bookmark)
        }
    }

    fun updateAllBookmarks(updateAction: (Bookmark) -> Bookmark): Job = coroutineScope.launch {
        val original = repository.getAllBookmarksOnce()
        val updated = original.map(updateAction)
        if (original != updated) {
            repository.bulkUpdate(updated)
        }
    }

    private suspend fun Bookmark.saveBookmarkWithNewInterval(): Bookmark {
        val minMillis = min.inWholeMilliseconds
        val maxMillis = max.inWholeMilliseconds
        val interval = if (maxMillis > minMillis) {
            Random.nextLong(minMillis, maxMillis + 1)
        } else {
            minMillis
        }
        return copy(intervalEnd = interval + System.currentTimeMillis())
            .also { repository.insertOrUpdateBookmark(it) }
    }

    private suspend fun cancelTimer(bookmarkId: Long) =
        repository.getBookmarkByIdOnce(bookmarkId)?.also {
            Timber.d("cancel bookmark $it")
            repository.insertOrUpdateBookmark(it.reset())
            notificationCoordinator.cancelAllNotifications(it)
            alarmCoordinator.cancelAlarm(it)
        }

    fun fetchAllBookmarks() = repository.getAllBookmarks()

}