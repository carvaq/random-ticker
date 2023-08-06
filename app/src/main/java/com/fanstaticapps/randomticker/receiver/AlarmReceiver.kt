package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var tickerNotificationManager: TickerNotificationManager

    @Inject
    internal lateinit var repository: BookmarkRepository


    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Alarm received")
        val bookmarkId = intent.getBookmarkId()
        if (bookmarkId != null) {
            scope.launch(Dispatchers.Default) {
                repository.getBookmarkById(bookmarkId).firstOrNull()?.let { bookmark ->
                    tickerNotificationManager.showNotificationWithFullScreenIntent(
                        context,
                        bookmark
                    )
                }
            }
        } else {
            Timber.e("No bookmark ID passed")
        }
    }


}
