package com.fanstaticapps.randomticker.ui.klaxon

import android.content.Intent
import android.os.Bundle
import android.view.animation.*
import androidx.activity.viewModels
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TimerHelper
import com.fanstaticapps.randomticker.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_klaxon.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readExtras(intent)

        setContentView(R.layout.activity_klaxon)
        updateScreenStatus()

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
        timerHelper.newAlarmFromBookmark(this, bookmark)

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
            }
            is KlaxonViewState.ElapsedTimeUpdate -> {
                showElapsedTime(viewState.elapsedTime)
            }
            is KlaxonViewState.TickerFinished -> {
                timerHelper.startNotification(this, viewState.bookmark)

                tvElapsedTime.isEnabled = false

                showElapsedTime(viewState.elapsedTime)
                laWaiting.cancelAnimation()

                mlKlaxon.transitionToEnd()
                animatePulsator()
            }

            is KlaxonViewState.TickerCanceled -> {
                timerHelper.cancelTicker(this)

                laWaiting.cancelAnimation()
                ivPulsatorDismiss.animation.cancel()

                startActivity(IntentHelper.getMainActivity(this))
                overridePendingTransition(0, 0)
                finish()

            }
            is KlaxonViewState.TickerStopped -> {
                laWaiting.cancelAnimation()
                ivPulsatorDismiss.animation.cancel()
            }
        }
    }

    private fun animatePulsator() {
        val animationSet = AnimationSet(false)
        val alphaAnimation = AlphaAnimation(0f, 0.5f).apply {
            fillAfter = true
            duration = 2000
            interpolator = LinearInterpolator()
            repeatCount = 5
        }
        val scaleAnimation = ScaleAnimation(0f, 4f, 0f, 4f).apply {
            fillAfter = true
            duration = 2000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = 5
        }
        animationSet.addAnimation(alphaAnimation)
        animationSet.addAnimation(scaleAnimation)

        ivPulsatorDismiss.animation = animationSet
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