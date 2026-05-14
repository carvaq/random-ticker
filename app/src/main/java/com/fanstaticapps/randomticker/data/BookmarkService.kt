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
import kotlin.time.Clock

class BookmarkService(
    private val repository: BookmarkRepository,
    private val notificationCoordinator: NotificationCoordinator,
    private val alarmCoordinator: AlarmCoordinator,
    private val clock: Clock = Clock.System,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) {
    
    fun getBookmarkById(bookmarkId: Long): Flow<Bookmark> = repository.getBookmarkById(bookmarkId)
        .flowOn(Dispatchers.IO)
        .filterNotNull()
    
    fun save(bookmark: Bookmark): Job = coroutineScope.launch {
        repository.insertOrUpdateBookmark(bookmark)
    }
    
    suspend fun createNew(): Long {
        val newBookmark = Bookmark()
        val id = repository.insertOrUpdateBookmark(newBookmark)
        notificationCoordinator.triggerNotificationChannelNotification(newBookmark.copy(id = id))
        return id
    }
    
    fun updateWithIntervalEnded(bookmarkId: Long): Job = coroutineScope.launch {
        repository.getBookmarkByIdOnce(bookmarkId)?.let {
            notificationCoordinator.cancelAllNotifications(it)
            notificationCoordinator.showKlaxonNotification(it)
            if (it.autoRepeat) {
                scheduleBookmark(it, false)
            }
        }
    }
    
    fun scheduleAlarm(bookmarkId: Long, isManuallyTriggered: Boolean): Job = coroutineScope.launch {
        repository.getBookmarkByIdOnce(bookmarkId)?.let {
            scheduleBookmark(it, isManuallyTriggered)
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
    
    fun fetchAllBookmarks() = repository.getAllBookmarks()
    private suspend fun scheduleBookmark(currentBookmark: Bookmark, isManuallyTriggered: Boolean) {
        if (!isManuallyTriggered && currentBookmark.autoRepeat) {
            delay(currentBookmark.autoRepeatInterval)
        }
        
        val newIntervalEnd = calculateNewIntervalEnd(currentBookmark)
        val updatedBookmark = currentBookmark.copy(intervalEnd = newIntervalEnd)
        
        Timber.d("creating a new ticker for bookmark $updatedBookmark")
        repository.insertOrUpdateBookmark(updatedBookmark)
        notificationCoordinator.cancelAllNotifications(updatedBookmark)
        
        Timber.d("showing running ticker notification")
        notificationCoordinator.showRunningNotification(updatedBookmark)
        alarmCoordinator.scheduleAlarm(updatedBookmark)
    }
    
    private fun calculateNewIntervalEnd(bookmark: Bookmark): Long {
        val minMillis = bookmark.min.inWholeMilliseconds
        val maxMillis = bookmark.max.inWholeMilliseconds
        val interval = if (maxMillis > minMillis) {
            Random.nextLong(minMillis, maxMillis + 1)
        } else {
            minMillis
        }
        return interval + clock.now().toEpochMilliseconds()
    }
    
    private suspend fun cancelTimer(bookmarkId: Long): Bookmark? =
        repository.getBookmarkByIdOnce(bookmarkId)?.also {
            Timber.d("cancel bookmark $it")
            repository.insertOrUpdateBookmark(it.reset())
            notificationCoordinator.cancelAllNotifications(it)
            alarmCoordinator.cancelAlarm(it)
        }
    
}