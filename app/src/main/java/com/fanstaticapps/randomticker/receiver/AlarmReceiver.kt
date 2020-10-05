package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.extensions.isAppInBackground
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: TickerNotificationManager

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var repository: BookmarkRepository

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Alarm received")

        if (context.isAppInBackground()) {
            repository.getBookmarkById(userPreferences.currentSelectedId).value?.let { bookmark ->
                notificationManager.showKlaxonNotification(context, bookmark)
            }
        }
    }

}
