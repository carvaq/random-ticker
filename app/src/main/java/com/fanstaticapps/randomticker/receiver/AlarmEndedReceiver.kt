package com.fanstaticapps.randomticker.receiver

import android.content.Context

class AlarmEndedReceiver : BaseReceiver() {
    override fun BroadcastWrapper.handleBookmark(context: Context, bookmarkId: Long) {
        bookmarkService.intervalEnded(bookmarkId)
    }

    companion object {
        const val ACTION = "com.fanstaticapps.randomticker.ALARM"
    }
}
