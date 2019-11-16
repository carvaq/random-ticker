package com.fanstaticapps.randomticker.ui.ticker

import android.content.Intent
import android.os.Bundle
import com.fanstaticapps.randomticker.TimerHelper
import com.fanstaticapps.randomticker.ui.BaseActivity
import javax.inject.Inject

open class KlaxonBaseActivity : BaseActivity() {

    companion object {
        const val EXTRA_TIME_ELAPSED = "extra_time_elapsed"
    }

    var timeElapsed: Boolean = false

    @Inject
    lateinit var timerHelper: TimerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readExtras(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            readExtras(intent)
        }
    }

    private fun readExtras(intent: Intent) {
        timeElapsed = intent.getBooleanExtra(EXTRA_TIME_ELAPSED, false)
    }

}