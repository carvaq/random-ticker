package com.fanstaticapps.randomticker.receiver

import android.content.Context

class AlarmEndedReceiver : BaseReceiver() {


    override fun handleBookmark(context: Context, bookmarkId: Long) {
        bookmarkService.intervalEnded(context, bookmarkId)
    }
}
