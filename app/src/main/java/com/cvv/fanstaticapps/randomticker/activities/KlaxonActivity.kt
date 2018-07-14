package com.cvv.fanstaticapps.randomticker.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.GONE
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.RotateAnimation
import com.cvv.fanstaticapps.randomticker.PREFS
import com.cvv.fanstaticapps.randomticker.R
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper.Companion.ONE_SECOND_IN_MILLIS
import io.github.kobakei.grenade.annotation.Extra
import io.github.kobakei.grenade.annotation.Navigator
import kotlinx.android.synthetic.main.activity_klaxon.*
import timber.log.Timber
import java.lang.Math.abs
import javax.inject.Inject


@Navigator
class KlaxonActivity : BaseActivity() {

    companion object {
        private const val ANIMATION_DURATION = 750
        private const val TAG = "KlaxonActivity"
    }

    @JvmField
    @Extra
    var timeElapsed: Boolean = false

    @Inject
    lateinit var timerHelper: TimerHelper

    private var playingAlarmSound: Ringtone? = null
    private var waitingIconAnimation: Animation? = null
    //timestamp when the timer should ring
    private var intervalFinished: Long = 0
    private var countDownTimer: CountDownTimer? = null
    private var showElapsedTime: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_klaxon)
        KlaxonActivityNavigator.inject(this, intent)

        intervalFinished = PREFS.intervalFinished
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        dismissButton.setOnClickListener {
            cancelEverything()
            timerHelper.cancelNotificationAndGoBack(this, PREFS)
        }

        if (timeElapsed) {
            timerFinished()
        } else {
            startCountDownTimer()
            elapsedTime.setOnClickListener({ showElapsedTime = true })
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        KlaxonActivityNavigator.inject(this, intent)
        if (timeElapsed && countDownTimer != null) {
            countDownTimer!!.cancel()
            timerFinished()
        }
    }

    override fun onStop() {
        super.onStop()
        cancelEverything()
    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(intervalFinished - System.currentTimeMillis(), ONE_SECOND_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                if (showElapsedTime) {
                    val millisSinceStarted = abs(intervalFinished - System.currentTimeMillis() - PREFS.interval)
                    val elapsedMillis = timerHelper
                            .getFormattedElapsedMilliseconds(millisSinceStarted)
                    elapsedTime.text = elapsedMillis
                }
            }

            override fun onFinish() {
                timerFinished()
                PREFS.currentlyTickerRunning = false
                countDownTimer = null
            }
        }
        countDownTimer!!.start()
        startBellAnimation()
    }

    private fun timerFinished() {
        timerHelper.cancelNotification(this, PREFS)
        playRingtone()
        if (!pulsator.isStarted) {
            hideBellAndMoveCancelButton()
        }
    }

    private fun hideBellAndMoveCancelButton() {
        waitingIconAnimation?.cancel()
        elapsedTime.visibility = GONE
        waitingIcon.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(ANIMATION_DURATION.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        waitingIcon!!.visibility = View.GONE
                    }
                }).start()

        pulsator.animate()
                .scaleX(1.5f)
                .setStartDelay(100)
                .scaleY(1.5f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        pulsator.start()
                    }
                }).duration = ANIMATION_DURATION.toLong()
    }

    private fun cancelEverything() {
        playingAlarmSound?.stop()
        waitingIconAnimation?.cancel()
        countDownTimer?.cancel()
    }

    private fun startBellAnimation() {
        waitingIconAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.55f)
        waitingIconAnimation!!.repeatCount = Animation.INFINITE
        waitingIconAnimation!!.interpolator = CycleInterpolator(1f)
        waitingIconAnimation!!.duration = 4000
        timerHand.startAnimation(waitingIconAnimation)
    }


    private fun playRingtone() {
        if (playingAlarmSound == null) {
            try {
                val uri = Uri.parse(PREFS.alarmRingtone)
                playingAlarmSound = RingtoneManager.getRingtone(applicationContext, uri)
                playingAlarmSound!!.play()
            } catch (e: Exception) {
                Timber.e(e, "Error while trying to play alarm sound")
            }
        } else {
            toast(R.string.bell_ringing)
        }
    }

}