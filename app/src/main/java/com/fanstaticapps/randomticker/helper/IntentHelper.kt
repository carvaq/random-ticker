package com.fanstaticapps.randomticker.helper

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.getActivity
import android.app.PendingIntent.getBroadcast
import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.EXTRA_BOOKMARK_ID
import com.fanstaticapps.randomticker.receiver.CreateAlarmReceiver
import com.fanstaticapps.randomticker.ui.cancel.CancelActivity
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonActivity
import com.fanstaticapps.randomticker.ui.main.MainActivity
import kotlin.reflect.KClass

object IntentHelper {

    fun getOpenAppPendingIntent(context: Context, requestCode: Int): PendingIntent {
        return getActivity(
            context,
            requestCode,
            Intent(context, MainActivity::class.java),
            FLAG_UPDATE_CURRENT
        )
    }

    fun getCancelActionPendingIntent(
        context: Context,
        requestCode: Int,
        bookmark: Bookmark
    ): PendingIntent {
        return getActivity(
            context,
            requestCode,
            context.intent(CancelActivity::class, bookmark),
            FLAG_UPDATE_CURRENT
        )
    }

    fun getRepeatReceiverPendingIntent(context: Context, bookmark: Bookmark): PendingIntent {
        return getBroadcast(
            context,
            REQUEST_CODE_REPEAT,
            context.intent(CreateAlarmReceiver::class, bookmark),
            FLAG_UPDATE_CURRENT
        )
    }

    fun getFullScreenIntent(context: Context, bookmark: Bookmark): PendingIntent {
        return getActivity(
            context,
            bookmark.klaxonNotificationId,
            context.intent(KlaxonActivity::class, bookmark),
            FLAG_IMMUTABLE
        )
    }

    private fun Context.intent(kclass: KClass<*>, bookmark: Bookmark) =
        Intent(this, kclass.java).apply {
            putExtra(EXTRA_BOOKMARK_ID, bookmark.id)
        }

    private const val FLAG_UPDATE_CURRENT = PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
    private const val REQUEST_CODE_REPEAT = 112
}