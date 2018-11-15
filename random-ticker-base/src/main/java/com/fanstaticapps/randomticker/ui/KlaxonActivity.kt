package com.fanstaticapps.randomticker.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fanstaticapps.common.activities.KlaxonBaseActivity
import com.fanstaticapps.common.helper.WakeLocker
import com.fanstaticapps.common.view.AnimatorEndListener
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.mvp.KlaxonPresenter
import com.fanstaticapps.randomticker.mvp.KlaxonPresenter.ViewState
import com.fanstaticapps.randomticker.mvp.KlaxonView
import kotlinx.android.synthetic.main.activity_klaxon.*
import timber.log.Timber


class KlaxonActivity : KlaxonBaseActivity(), KlaxonView {

    private val animationDuration = 750

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    private lateinit var presenter: KlaxonPresenter
    private var elapsedTimeNeedsAnimation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WakeLocker.acquireLock(this)

        setContentView(R.layout.activity_klaxon)

        enableShowWhenLocked()

        presenter = KlaxonPresenter(this, PREFS.intervalFinished, timeElapsed)
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        presenter.update(timeElapsed)
    }

    override fun onResume() {
        super.onResume()
        dismissButton.setOnClickListener {
            presenter.cancelTimer()
        }

        presenter.init()
    }

    override fun onPause() {
        super.onPause()
        WakeLocker.release()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun render(viewState: ViewState) {
        Timber.d("Rendering  ${viewState.javaClass.simpleName}")
        when (viewState) {
            is ViewState.TimerStarted -> {
                startWaitingIconAnimation()
                elapsedTime.setOnClickListener { presenter.showElapsedTime = true }
            }
            is ViewState.ElapseTimeUpdate -> {
                showElapsedTime(viewState.elapsedTime)
            }
            is ViewState.TimerFinished -> {
                timerHelper.cancelNotification(this, PREFS)
                if (!pulsator.isStarted) {
                    startHideWaitingIconAnimation()
                    startPulsatorAnimation()
                }
                showElapsedTime(viewState.elapsedTime)
                playRingtone()
                vibrate()
                elapsedTime.isEnabled = false
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
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
        waitingIcon.cancelAnimation()
        vibrator?.cancel()
    }

    private fun showElapsedTime(elapsedTimeInMillis: String?) {
        if (elapsedTimeInMillis == null) return
        elapsedTime.text = elapsedTimeInMillis
        if (elapsedTimeNeedsAnimation) {
            elapsedTimeNeedsAnimation = false
            val startSize = resources.getDimensionPixelSize(R.dimen.elepsedTimeTextSize).toFloat()
            val endSize = resources.getDimensionPixelSize(R.dimen.elepsedTimeTextSizeZoomed).toFloat()
            val textSizeAnimator = ValueAnimator.ofFloat(startSize, endSize)
            textSizeAnimator.addUpdateListener {
                val animatedValue = it.animatedValue as Float
                elapsedTime.textSize = animatedValue
            }

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(textSizeAnimator)
            animatorSet.duration = 1200
            animatorSet.addListener(object : AnimatorEndListener() {
                override fun onAnimationEnd(p0: Animator?) {
                    startWaitingIconAnimation()
                }
            })
            animatorSet.start()
        }
    }

    private fun startWaitingIconAnimation() {
        waitingIcon.playAnimation()
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

    private fun startHideWaitingIconAnimation() {
        waitingIcon.cancelAnimation()
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

    private fun playRingtone() {
        if (mediaPlayer == null) {
            try {
                val uri = Uri.parse(PREFS.alarmRingtone)

                mediaPlayer = MediaPlayer().apply {
                    setDataSource(applicationContext, uri)
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
        } else {
            toast(R.string.bell_ringing)
        }
    }

    private fun vibrate() {
        if (PREFS.vibrator) {
            vibrator = vibrator ?: ContextCompat.getSystemService(this, Vibrator::class.java)
            val vibratePattern = longArrayOf(0, 100, 800, 600, 800, 800, 800, 1000)
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator?.vibrate(VibrationEffect.createWaveform(vibratePattern, 0))
            } else {
                vibrator?.vibrate(vibratePattern, 0)
            }
        }
    }

}