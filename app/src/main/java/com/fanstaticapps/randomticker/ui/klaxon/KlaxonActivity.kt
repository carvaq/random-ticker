package com.fanstaticapps.randomticker.ui.klaxon

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.fanstaticapps.randomticker.R
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
        if (timeElapsed) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)

        } else {
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
                timerHelper.newAlarmFromBookmark(this, it)

                finish()
                startActivity(intent.apply { putExtra(EXTRA_TIME_ELAPSED, false) })
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left)
            })
        }

    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private fun render(viewState: KlaxonViewState) {
        Timber.d("Rendering  ${viewState.javaClass.simpleName}")
        when (viewState) {
            is KlaxonViewState.TimerStarted -> {
                startWaitingIconAnimation()
                tvElapsedTime.setOnClickListener { viewModel.showElapsedTime = true }
            }
            is KlaxonViewState.ElapseTimeUpdate -> {
                showElapsedTime(viewState.elapsedTime)
            }
            is KlaxonViewState.TimerFinished -> {
                timerHelper.startNotification(this, viewState.bookmark)

                tvElapsedTime.isEnabled = false

                showElapsedTime(viewState.elapsedTime)
                laWaiting.cancelAnimation()

                mlKlaxon.transitionToEnd()

                if (!plDismiss.isStarted) {
                    plDismiss.start()
                }

            }
            is KlaxonViewState.TimerCanceled -> {
                timerHelper.cancelTicker(this)

                startActivity(IntentHelper.getMainActivity(this))
                overridePendingTransition(0, 0)
                finish()

                laWaiting.cancelAnimation()
            }
            is KlaxonViewState.TimerStopped -> {
                laWaiting.cancelAnimation()
            }
        }
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