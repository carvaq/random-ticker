package com.fanstaticapps.randomticker.ui

import android.os.Bundle
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TimerHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CancelActivity : BaseActivity() {
    @Inject
    lateinit var timerHelper: TimerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timerHelper.cancelTicker(this)

        startActivity(IntentHelper.getMainActivity(this))
        overridePendingTransition(0, 0)
        finish()
    }
}
