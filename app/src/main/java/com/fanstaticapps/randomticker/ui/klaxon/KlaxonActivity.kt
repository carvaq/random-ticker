package com.fanstaticapps.randomticker.ui.klaxon

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.viewModels
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TimerHelper
import com.fanstaticapps.randomticker.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_klaxon.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class KlaxonActivity : BaseActivity() {

    companion object {
        const val EXTRA_TIME_ELAPSED = "extra_time_elapsed"
    }

    @Inject
    lateinit var timerHelper: TimerHelper

    private val viewModel: KlaxonViewModel by viewModels()

    private var timeElapsed: Boolean = false
    private val pulsatorAnimation = AnimatorSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readExtras(intent)

        setContentView(R.layout.activity_klaxon)
        updateScreenStatus()
        prepareAnimation()

        viewModel.getCurrentViewState().observe(this) {
            render(it)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            readExtras(intent)
        }
        updateScreenStatus()
    }

    private fun prepareAnimation() {
        val imageXAnimator = ObjectAnimator.ofFloat(ivPulsatorDismiss, "scaleX", 4f)
        val imageYAnimator = ObjectAnimator.ofFloat(ivPulsatorDismiss, "scaleY", 4f)
        val imageAlphaAnimator = ObjectAnimator.ofFloat(ivPulsatorDismiss, "alpha", 0.6f)
        val animators = listOf(imageXAnimator, imageYAnimator, imageAlphaAnimator)
        pulsatorAnimation.apply {
            playTogether(imageXAnimator, imageYAnimator, imageAlphaAnimator)
            duration = 1800
            interpolator = AccelerateDecelerateInterpolator()
        }
        animators.forEach { animator ->
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.repeatMode = ObjectAnimator.REVERSE
        }
    }

    private fun updateScreenStatus() {
        if (!timeElapsed) {
            mlKlaxon.transitionToStart()
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Alarm time elapsed?  $timeElapsed")
        btnDismiss.setOnClickListener {
            viewModel.cancelTimer()
        }

        btnRepeat.setOnClickListener {
            viewModel.currentBookmark.observe(this, {
                autoRepeatTicker(it)
            })
        }

    }

    private fun autoRepeatTicker(bookmark: Bookmark) {
        timerHelper.newTickerFromBookmark(this, bookmark)

        finish()
        startActivity(intent.apply { putExtra(EXTRA_TIME_ELAPSED, false) })
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left)
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private fun render(viewState: KlaxonViewState) {
        Timber.d("Rendering  ${viewState.javaClass.simpleName}")
        when (viewState) {
            is KlaxonViewState.TickerStarted -> {
                startWaitingIconAnimation()
                tvElapsedTime.setOnClickListener { viewModel.showElapsedTime = true }
                if (viewState.bookmark.autoRepeat) {
                    btnRepeat.visibility = View.GONE
                }
            }
            is KlaxonViewState.ElapsedTimeUpdate -> {
                showElapsedTime(viewState.elapsedTime)
            }
            is KlaxonViewState.TickerFinished -> {
                showElapsedTime(viewState.elapsedTime)
                startTickerRinging(viewState.bookmark)
            }

            is KlaxonViewState.TickerCanceled -> {
                timerHelper.cancelTicker(this)
                stopTickerRunning()

                startActivity(IntentHelper.getMainActivity(this))
                overridePendingTransition(0, 0)
                finish()
            }
            is KlaxonViewState.TickerRepeat -> {
                startTickerRinging(viewState.bookmark)

                GlobalScope.launch {
                    delay(2000)
                    autoRepeatTicker(viewState.bookmark)
                }
            }
            is KlaxonViewState.TickerStopped -> {
                stopTickerRunning()
            }
            KlaxonViewState.TickerNoop -> {
            }
        }
    }

    private fun stopTickerRunning() {
        laWaiting.cancelAnimation()
        pulsatorAnimation.cancel()
    }

    private fun startTickerRinging(bookmark: Bookmark) {
        timerHelper.startNotification(this, bookmark)

        tvElapsedTime.isEnabled = false
        laWaiting.cancelAnimation()
        mlKlaxon.transitionToEnd()
        pulsatorAnimation.start()
    }


    private fun readExtras(intent: Intent) {
        viewModel.setTimeElapsed(intent.getBooleanExtra(EXTRA_TIME_ELAPSED, false))
    }

    private fun showElapsedTime(elapsedTimeInMillis: String?) {
        elapsedTimeInMillis?.let { tvElapsedTime.text = elapsedTimeInMillis }
    }

    private fun startWaitingIconAnimation() {
        laWaiting.playAnimation()
    }

}