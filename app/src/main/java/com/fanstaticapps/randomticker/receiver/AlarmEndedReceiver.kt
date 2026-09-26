package com.fanstaticapps.randomticker.receiver

class AlarmEndedReceiver : BaseReceiver() {
    override fun BroadcastWrapper.handleBookmark(bookmarkId: Long) = bookmarkService.updateWithIntervalEnded(bookmarkId)

    companion object {
        const val ACTION = "com.fanstaticapps.randomticker.ALARM"
    }
}
