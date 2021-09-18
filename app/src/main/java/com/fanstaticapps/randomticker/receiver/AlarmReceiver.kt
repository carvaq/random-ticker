package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: TickerNotificationManager

    @Inject
    lateinit var tickerPreferences: TickerPreferences

    @Inject
    lateinit var repository: BookmarkRepository

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Alarm received")
        runBlocking {
            repository.getBookmarkById(tickerPreferences.currentSelectedId)?.let { bookmark ->
                Timber.d("Showing notification for bookmark")
                notificationManager.showKlaxonNotification(context, bookmark)
            }
        }
    }

}
