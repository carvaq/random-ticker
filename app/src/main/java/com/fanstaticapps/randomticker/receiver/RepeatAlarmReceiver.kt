package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.helper.TimerHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RepeatAlarmReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob())

    @Inject
    internal lateinit var timerHelper: TimerHelper

    @Inject
    internal lateinit var tickerPreferences: TickerPreferences

    @Inject
    internal lateinit var repository: BookmarkRepository


    override fun onReceive(context: Context, intent: Intent) {
        scope.launch(Dispatchers.Default) {
            repository.getBookmarkById(tickerPreferences.currentSelectedId)?.let { bookmark ->
                Timber.d("Creating a new alarm!")
                if (!bookmark.autoRepeat) {
                    timerHelper.startTicker(bookmark)
                    LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(Intent(TICKER_RESTARTED))
                }
            }
        }
    }

    companion object {
        const val TICKER_RESTARTED = "TICKER_RESTARTED"
    }
}