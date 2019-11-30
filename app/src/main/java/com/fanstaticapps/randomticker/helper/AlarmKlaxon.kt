package com.fanstaticapps.randomticker.helper

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.fanstaticapps.randomticker.PREFS
import timber.log.Timber

internal object AlarmKlaxon {

    private var mediaPlayer: MediaPlayer? = null

    fun stop() {
        Timber.v("AlarmKlaxon.stop()")

        stopMediaPlayer()

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

}