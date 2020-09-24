package com.fanstaticapps.randomticker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.helper.TimerHelper
import timber.log.Timber
import javax.inject.Inject

class RepeatAlarmReceiver : BroadcastReceiver() {

    @Inject
    internal lateinit var timerHelper: TimerHelper

    @Inject
    internal lateinit var userPreferences: UserPreferences

    @Inject
    internal lateinit var repository: BookmarkRepository


    override fun onReceive(context: Context, intent: Intent) {
        repository.getBookmarkById(userPreferences.currentSelectedId).value?.let { bookmark ->

            Timber.d("Creating a new alarm!")
            timerHelper.newAlarmFromBookmark(context, bookmark)
        }
    }
}
