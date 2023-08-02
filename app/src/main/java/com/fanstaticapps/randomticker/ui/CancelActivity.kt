package com.fanstaticapps.randomticker.ui

import android.os.Bundle
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TimerHelper
import com.fanstaticapps.randomticker.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CancelActivity : BaseActivity() {
    @Inject
    lateinit var timerHelper: TimerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bookmarkId: Long? =
            intent.getLongExtra(MainActivity.EXTRA_BOOKMARK_ID, 0).takeUnless { it == 0L }
        timerHelper.cancelTicker(bookmarkId)
        startActivity(IntentHelper.getMainActivity(this, bookmarkId))
        noOpenOrCloseTransitions()
        finish()
    }
}