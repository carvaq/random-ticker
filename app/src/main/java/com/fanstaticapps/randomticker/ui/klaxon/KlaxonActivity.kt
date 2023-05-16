package com.fanstaticapps.randomticker.ui.klaxon

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.databinding.ActivityKlaxonBinding
import com.fanstaticapps.randomticker.extensions.viewBinding
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TimerHelper
import com.fanstaticapps.randomticker.receiver.RepeatAlarmReceiver
import com.fanstaticapps.randomticker.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
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
    private val binding by viewBinding(ActivityKlaxonBinding::inflate)

    private val pulsatorAnimation = AnimatorSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setBackgroundDrawableResource(R.drawable.klaxon_background)
        super.onCreate(savedInstanceState)
        readExtras(intent)

        setContentView(binding.root)
        updateScreenStatus()
        prepareAnimation()

        viewModel.currentStateLD.observe(this) { render(it) }
        registerBroadcast()
    }

    private fun registerBroadcast() = with(LocalBroadcastManager.getInstance(this)) {
        registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    unregisterReceiver(this)
                    reloadKlaxonView()
                }
            },
            IntentFilter(RepeatAlarmReceiver.TICKER_RESTARTED)
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            readExtras(intent)
        }
        updateScreenStatus()
    }

    private fun prepareAnimation() {
        val imageXAnimator = ObjectAnimator.ofFloat(binding.ivPulsatorDismiss, "scaleX", 3f)
        val imageYAnimator = ObjectAnimator.ofFloat(binding.ivPulsatorDismiss, "scaleY", 3f)
        val imageAlphaAnimator = ObjectAnimator.ofFloat(binding.ivPulsatorDismiss, "alpha", 0.6f)
        val animators = listOf(imageXAnimator, imageYAnimator, imageAlphaAnimator)

        pulsatorAnimation.apply {
            playTogether(imageXAnimator, imageYAnimator, imageAlphaAnimator)
            duration = 1800
            interpolator = FastOutSlowInInterpolator()
        }
        animators.forEach { animator ->
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.repeatMode = ObjectAnimator.REVERSE
        }
    }

    private fun updateScreenStatus() {
        binding.mlKlaxon.transitionToStart()
    }

    override fun onResume() {
        super.onResume()
        binding.btnDismiss.setOnClickListener {
            viewModel.cancelTimer()
        }

        binding.btnRepeat.setOnClickListener {
            viewModel.currentBookmark.observe(this) {
                autoRepeatTicker(it)
            }
        }

    }

    private fun autoRepeatTicker(bookmark: Bookmark) {
        timerHelper.newTickerFromBookmark(this, bookmark)

        reloadKlaxonView()
    }

    private fun reloadKlaxonView() {
        finish()
        startActivity(intent.apply { putExtra(EXTRA_TIME_ELAPSED, false) })
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
                binding.tvElapsedTime.setOnClickListener { viewModel.showElapsedTime = true }
                if (viewState.bookmark.autoRepeat) {
                    binding.btnRepeat.visibility = View.GONE
                }
            }
            is KlaxonViewState.ElapsedTimeUpdate -> {
                showElapsedTime(viewState.elapsedTime)
            }
            is KlaxonViewState.TickerFinished -> {
                showElapsedTime(viewState.elapsedTime)
                startTickerRinging()

                if (viewState.bookmark.autoRepeat) {
                    lifecycleScope.launch {
                        delay(2000)
                        autoRepeatTicker(viewState.bookmark)
                    }
                }
            }
            is KlaxonViewState.TickerCanceled -> {
                timerHelper.cancelTicker(this)
                stopTickerRunning()

                startActivity(IntentHelper.getMainActivity(this))
                noOpenOrCloseTransitions()
                finish()
            }
            is KlaxonViewState.TickerStopped -> {
                stopTickerRunning()
            }
            KlaxonViewState.TickerNoop -> {
            }
        }
    }

    private fun stopTickerRunning() {
        binding.laWaiting.cancelAnimation()
        pulsatorAnimation.cancel()
    }

    private fun startTickerRinging() {
        timerHelper.startNotification(this)

        binding.tvElapsedTime.isEnabled = false
        binding.laWaiting.cancelAnimation()
        binding.mlKlaxon.transitionToEnd()
        pulsatorAnimation.start()
    }


    private fun readExtras(intent: Intent) {
        viewModel.setTimeElapsed(intent.getBooleanExtra(EXTRA_TIME_ELAPSED, false))
    }

    private fun showElapsedTime(elapsedTimeInMillis: String?) {
        elapsedTimeInMillis?.let { binding.tvElapsedTime.text = elapsedTimeInMillis }
    }

    private fun startWaitingIconAnimation() {
        binding.laWaiting.playAnimation()
    }

}