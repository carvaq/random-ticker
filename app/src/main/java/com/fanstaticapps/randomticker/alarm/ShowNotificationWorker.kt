package com.fanstaticapps.randomticker.alarm

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import timber.log.Timber

class ShowNotificationWorker(private val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Timber.d("Alarm notification deployed")

        val notificationManager = TickerNotificationManager(IntentHelper())
        notificationManager.showKlaxonNotification(appContext)

        return Result.success()
    }
}
