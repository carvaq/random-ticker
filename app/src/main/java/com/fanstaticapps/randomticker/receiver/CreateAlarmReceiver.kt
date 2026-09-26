package com.fanstaticapps.randomticker.receiver

class CreateAlarmReceiver : BaseReceiver() {
    override fun BroadcastWrapper.handleBookmark(
        bookmarkId: Long,
    ) =
        bookmarkService.scheduleAlarm(bookmarkId, false)

}