package com.fanstaticapps.randomticker.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.extensions.getVibrator
import com.fanstaticapps.randomticker.extensions.isAtLeastAndroid26
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import timber.log.Timber

internal object AlarmKlaxon {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    fun stop(context: Context) {
        Timber.v("AlarmKlaxon.stop()")

        stopMediaPlayer()

        vibrator?.cancel()
    }

    private fun stopMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun start(context: Context) {
        // Make sure we are stopped before starting
        Timber.v("AlarmKlaxon.start()")

        if (PREFS.alarmRingtone.isNotEmpty()) {
            playAlarm(context)
        }

        if (PREFS.vibrator) {
            vibrate(context)
        }
    }

    private fun playAlarm(context: Context) {
        try {
            val uri = Uri.parse(PREFS.alarmRingtone)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                setAudioAttributes(AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error while trying to play alarm sound")
        }
    }


    private fun vibrate(context: Context) {
        val vibratePattern = TickerNotificationManager.VIBRATION_PATTERN
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        vibrator = context.getVibrator()
        if (isAtLeastAndroid26()) {
            vibrator?.vibrate(VibrationEffect.createWaveform(vibratePattern, 0), audioAttributes)
        } else {
            vibrator?.vibrate(vibratePattern, 0, audioAttributes)
        }
    }


}