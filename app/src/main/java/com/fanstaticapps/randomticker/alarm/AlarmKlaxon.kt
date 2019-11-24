package com.fanstaticapps.randomticker.alarm

import android.annotation.TargetApi
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.Vibrator
import androidx.core.content.ContextCompat
import com.airbnb.lottie.utils.Utils
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import timber.log.Timber

internal object AlarmKlaxon {

    private var isStarted = false
    private var asyncRingtonePlayer: AsyncRingtonePlayer? = null

    fun stop(context: Context) {
        if (isStarted) {
            Timber.v("AlarmKlaxon.stop()")
            isStarted = false
            getAsyncRingtonePlayer(context).stop()
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).cancel()
        }
    }

    fun start(context: Context) {
        // Make sure we are stopped before starting
        stop(context)
        Timber.v("AlarmKlaxon.start()")

        if (!AlarmInstance.NO_RINGTONE_URI.equals(instance.mRingtone)) {
            val crescendoDuration = DataModel.getDataModel().getAlarmCrescendoDuration()
            getAsyncRingtonePlayer(context).play(instance.mRingtone, crescendoDuration)
        }

        if (instance.mVibrate) {
            val vibrator = getVibrator(context)
            if (Utils.isLOrLater()) {
                vibrateLOrLater(vibrator)
            } else {
                vibrator.vibrate(TickerNotificationManager.VIBRATION_PATTERN, 0)
            }
        }

        isStarted = true
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun vibrateLOrLater(vibrator: Vibrator) {
        vibrator.vibrate(TickerNotificationManager.VIBRATION_PATTERN, 0, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build())
    }

    private fun getVibrator(context: Context): Vibrator? {
        return ContextCompat.getSystemService(context, Vibrator::class.java)
    }

}