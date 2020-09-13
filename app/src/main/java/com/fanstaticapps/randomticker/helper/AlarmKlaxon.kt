package com.fanstaticapps.randomticker.helper

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.extensions.getVibrator
import com.fanstaticapps.randomticker.extensions.isAtLeastAndroid26
import timber.log.Timber

internal object AlarmKlaxon {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    fun stop(context: Context) {
        Timber.v("AlarmKlaxon.stop()")

        stopMediaPlayer()

        vibrator?.cancel()
        context.getVibrator()?.cancel()
    }

    private fun stopMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun start(context: Context, preferences: UserPreferences) {
        // Make sure we are stopped before starting
        Timber.v("AlarmKlaxon.start()")

        if (preferences.alarmRingtone.isNotEmpty()) {
            playAlarm(context, preferences)
        }

        if (preferences.vibrator) {
            vibrate(context)
        }
    }

    private fun vibrate(context: Context) {
        vibrator = context.getVibrator()
        if (isAtLeastAndroid26()) {
            vibrator?.vibrate(VibrationEffect.createWaveform(TickerNotificationManager.VIBRATION_PATTERN, 0))
        } else {
            vibrator?.vibrate(TickerNotificationManager.VIBRATION_PATTERN, 0, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
        }
    }

    private fun playAlarm(context: Context, preferences: UserPreferences) {
        if (mediaPlayer?.isPlaying == true) {
            return
        }
        try {
            val uri = Uri.parse(preferences.alarmRingtone)

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

}