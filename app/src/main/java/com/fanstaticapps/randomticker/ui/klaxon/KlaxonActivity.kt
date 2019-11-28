package com.fanstaticapps.randomticker.ui.klaxon

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TimerHelper
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonPresenter.ViewState
import com.fanstaticapps.randomticker.view.AnimatorEndListener
import kotlinx.android.synthetic.main.activity_klaxon.*
import timber.log.Timber
import javax.inject.Inject


class KlaxonActivity : BaseActivity(), KlaxonView {

    companion object {
        const val EXTRA_TIME_ELAPSED = "extra_time_elapsed"
        private const val ANIMATION_DURATION = 750
    }

    @Inject
    lateinit var notificationManager: TickerNotificationManager
    @Inject
    lateinit var timerHelper: TimerHelper

    private val presenter by lazy { KlaxonPresenter(this, PREFS.intervalFinished, timeElapsed) }

    private var elapsedTimeNeedsAnimation: Boolean = true
    private var timeElapsed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readExtras(intent)

        setContentView(R.layout.activity_klaxon)
        updateScreenStatus()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            readExtras(intent)
        }
        presenter.update(timeElapsed)
        updateScreenStatus()
    }

    private fun updateScreenStatus() {
        if (timeElapsed) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)

        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Alarm time elapsed?  $timeElapsed")
        dismissButton.setOnClickListener {
            presenter.cancelTimer()
        }

        presenter.init()
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
                notificationManager.cancelNotifications(this)

                if (!pulsator.isStarted) {
                    startHideWaitingIconAnimation()
                    startPulsatorAnimation()
                }
                showElapsedTime(viewState.elapsedTime)
                elapsedTime.isEnabled = false
            }
            is ViewState.TimerCanceled -> {
                cancelEverything()
                timerHelper.cancelNotificationsAndGoBack(this)
            }
            is ViewState.TimerStopped -> {
                cancelEverything()
            }
        }
    }


    private fun readExtras(intent: Intent) {
        timeElapsed = intent.getBooleanExtra(EXTRA_TIME_ELAPSED, false)
    }

    private fun cancelEverything() {
        waitingIcon.cancelAnimation()
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
        waitingIcon.enableMergePathsForKitKatAndAbove(true)
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
                }).duration = ANIMATION_DURATION.toLong()
    }

    private fun startHideWaitingIconAnimation() {
        waitingIcon.cancelAnimation()
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
    }

}