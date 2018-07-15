package com.cvv.fanstaticapps.randomticker.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.RotateAnimation
import com.cvv.fanstaticapps.randomticker.PREFS
import com.cvv.fanstaticapps.randomticker.R
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper
import com.cvv.fanstaticapps.randomticker.helper.WakeLocker
import com.cvv.fanstaticapps.randomticker.mvp.KlaxonPresenter
import com.cvv.fanstaticapps.randomticker.mvp.KlaxonPresenter.ViewState
import com.cvv.fanstaticapps.randomticker.mvp.KlaxonView
import io.github.kobakei.grenade.annotation.Extra
import io.github.kobakei.grenade.annotation.Navigator
import kotlinx.android.synthetic.main.activity_klaxon.*
import timber.log.Timber
import javax.inject.Inject


@Navigator
class KlaxonActivity : BaseActivity(), KlaxonView {

    private val animationDuration = 750

    @JvmField
    @Extra
    var timeElapsed: Boolean = false

    @Inject
    lateinit var timerHelper: TimerHelper

    private var playingAlarmSound: Ringtone? = null
    private var waitingIconAnimation: Animation? = null
    private var vibrator: Vibrator? = null

    private lateinit var presenter: KlaxonPresenter;
    private var elapsedTimeNeedsAnimation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WakeLocker.acquireLock(this)

        setContentView(R.layout.activity_klaxon)

        enableShowWhenLocked()

        presenter = KlaxonPresenter(this, PREFS.intervalFinished, timeElapsed)

        KlaxonActivityNavigator.inject(this, intent)
    }

    @Suppress("DEPRECATION")
    private fun enableShowWhenLocked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }
    }

    override fun onPause() {
        super.onPause()
        WakeLocker.release()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        dismissButton.setOnClickListener {
            presenter.cancelTimer()
        }

        presenter.init()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        KlaxonActivityNavigator.inject(this, intent)
        presenter.update(timeElapsed)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun render(viewState: ViewState) {
        when (viewState) {
            is ViewState.TimerStarted -> {
                startBellAnimation()
                elapsedTime.setOnClickListener { presenter.showElapsedTime = true }

            }
            is ViewState.ElapseTimeUpdate -> {
                elapsedTime.text = viewState.elapsedTime
            }
            is ViewState.TimerFinished -> {
                timerHelper.cancelNotification(this, PREFS)
                showElapsedTime(viewState.elapsedTime)
                playRingtone()
                vibrate()
                if (!pulsator.isStarted) {
                    hideWaitingIcon()
                    startPulsatorAnimation()
                }
            }
            is ViewState.TimerCanceled -> {
                cancelEverything()
                timerHelper.cancelNotificationAndGoBack(this, PREFS)
            }
            is ViewState.TimerStopped -> {
                cancelEverything()
            }
        }
    }

    private fun cancelEverything() {
        playingAlarmSound?.stop()
        waitingIconAnimation?.cancel()
        vibrator?.cancel()
    }

    private fun showElapsedTime(elapsedTimeInMillis: String) {
        elapsedTime.text = elapsedTimeInMillis
        if (elapsedTimeNeedsAnimation) {
            elapsedTimeNeedsAnimation = false
            val startSize = resources.getDimensionPixelSize(R.dimen.elepsedTimeTextSize).toFloat()
            val endSize = resources.getDimensionPixelSize(R.dimen.elepsedTimeTextSizeZoomed).toFloat()

            val animator = ValueAnimator.ofFloat(startSize, endSize)
            animator.duration = 1200


            animator.addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                elapsedTime.setTextSize(animatedValue)
            }

            animator.start()
        }
    }

    private fun hideWaitingIcon() {
        waitingIconAnimation?.cancel()
        waitingIcon.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(animationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        waitingIcon!!.visibility = View.GONE
                    }
                }).start()
    }

    private fun startPulsatorAnimation() {
        pulsator.animate()
                .scaleX(1.5f)
                .setStartDelay(100)
                .scaleY(1.5f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        pulsator.start()
                    }
                }).duration = animationDuration.toLong()
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

    @Suppress("DEPRECATION")
    private fun vibrate() {
        if (vibrator == null && PREFS.vibrator) {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator?.vibrate(VibrationEffect.createOneShot(230, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator?.vibrate(230);
            }
        }
    }

}