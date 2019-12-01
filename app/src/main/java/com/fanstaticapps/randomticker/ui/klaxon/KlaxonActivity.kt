package com.fanstaticapps.randomticker.ui.klaxon

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.TickerDatabase
import com.fanstaticapps.randomticker.helper.AlarmKlaxon
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import com.fanstaticapps.randomticker.helper.TimerHelper
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonPresenter.ViewState
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_klaxon.*
import timber.log.Timber
import javax.inject.Inject


class KlaxonActivity : BaseActivity(), KlaxonView {

    companion object {
        const val EXTRA_TIME_ELAPSED = "extra_time_elapsed"
    }

    @Inject
    lateinit var notificationManager: TickerNotificationManager
    @Inject
    lateinit var timerHelper: TimerHelper

    private val database by lazy { TickerDatabase.getInstance(this) }
    private val presenter by lazy { KlaxonPresenter(this, PREFS.intervalFinished, timeElapsed) }

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

        } else {
            mlKlaxon.transitionToStart()
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Alarm time elapsed?  $timeElapsed")
        btnDismiss.setOnClickListener {
            presenter.cancelTimer()
        }

        btnRepeat.setOnClickListener {
            database.tickerDataDao().getById(PREFS.currentSelectedId)
                    .subscribeOn(Schedulers.computation())
                    .doOnSuccess {
                        timerHelper.newAlarmFromBookmark(this, it)
                        AlarmKlaxon.stop()
                        finish()
                        recreate()
                    }.subscribe()
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
                tvElapsedTime.setOnClickListener { presenter.showElapsedTime = true }
            }
            is ViewState.ElapseTimeUpdate -> {
                showElapsedTime(viewState.elapsedTime)
            }
            is ViewState.TimerFinished -> {
                AlarmKlaxon.start(this)
                notificationManager.cancelNotifications(this)

                tvElapsedTime.isEnabled = false

                showElapsedTime(viewState.elapsedTime)
                laWaiting.cancelAnimation()

                mlKlaxon.transitionToEnd()

                if (!plDismiss.isStarted) {
                    plDismiss.start()
                }

            }
            is ViewState.TimerCanceled -> {
                laWaiting.cancelAnimation()
                timerHelper.cancelNotificationsAndGoBack(this)
            }
            is ViewState.TimerStopped -> {
                laWaiting.cancelAnimation()
            }
        }
    }


    private fun readExtras(intent: Intent) {
        timeElapsed = intent.getBooleanExtra(EXTRA_TIME_ELAPSED, false)
    }

    private fun showElapsedTime(elapsedTimeInMillis: String?) {
        elapsedTimeInMillis?.let { tvElapsedTime.text = elapsedTimeInMillis }
    }

    private fun startWaitingIconAnimation() {
        laWaiting.enableMergePathsForKitKatAndAbove(true)
        laWaiting.playAnimation()
    }

}