package com.fanstaticapps.randomticker.receiver

import android.content.Context
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAlarmReceiver : BaseReceiver() {

    override fun handleBookmark(context: Context, bookmarkId: Long) {
        bookmarkService.schedulerAlarm(context, bookmarkId, false)
    }

}