package com.cvv.fanstaticapps.randomticker.activities

import android.os.Bundle
import com.cvv.fanstaticapps.randomticker.PREFS
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper
import javax.inject.Inject

class CancelActivity : BaseActivity() {
    @Inject
    lateinit var timerHelper: TimerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timerHelper.cancelNotificationAndGoBack(this, PREFS)
    }
}
