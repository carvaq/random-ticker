package com.fanstaticapps.randomticker.receiver

import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmEndedReceiver : BaseReceiver() {
    override fun handleBookmark(context: Context, intent: Intent, bookmarkId: Long) {
        bookmarkService.intervalEnded(context, bookmarkId)
    }
}
