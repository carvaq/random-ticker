package com.fanstaticapps.randomticker.ui

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.net.toUri
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.extensions.getVibrator
import com.fanstaticapps.randomticker.extensions.isAtLeastAndroid26
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import com.fanstaticapps.randomticker.helper.TimerHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RingtonePlayingService : Service() {
    @Inject
    lateinit var preferences: TickerPreferences

    @Inject
    lateinit var timerHelper: TimerHelper

    @Inject
    lateinit var notificationManager: TickerNotificationManager


    @Inject
    lateinit var repository: BookmarkRepository

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var isRunning = false

    private val binder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        val service: RingtonePlayingService
            get() = this@RingtonePlayingService
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        runBlocking {
            val context = baseContext
            if (timerHelper.hasTickerExpired() && timerHelper.hasAValidTicker()) {
                showNotification()
                if (!isRunning) {
                    vibrator = vibrator ?: context.getVibrator()
                    mediaPlayer = preferences.alarmRingtone.takeIf { it.isNotBlank() }
                        ?.toUri()
                        ?.let { uri -> context.preparePlayer(uri) }
                }
                isRunning = true
            } else {
                isRunning = false
                notificationManager.cancelAllNotifications(context)
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                vibrator?.cancel()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun Context.preparePlayer(uri: Uri) = MediaPlayer.create(this, uri).apply {
        setOnCompletionListener {
            if (isRunning) {
                it.start()
                vibrate()
            }
        }
        setOnPreparedListener {
            vibrate()
        }
        start()
    }

    private fun vibrate() {
        if (isAtLeastAndroid26()) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(1000)
        }
    }

    private suspend fun showNotification() {
        repository.getBookmarkById(preferences.currentSelectedId)?.let { bookmark ->
            Timber.d("Showing notification for bookmark")
            notificationManager.showKlaxonNotification(this, bookmark)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

}