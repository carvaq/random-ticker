package com.fanstaticapps.randomticker.ui

import android.os.Bundle
import com.fanstaticapps.randomticker.TimerHelper
import com.fanstaticapps.randomticker.UserPreferences
import javax.inject.Inject

class CancelActivity : BaseActivity() {
    @Inject
    lateinit var timerHelper: TimerHelper
    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timerHelper.cancelNotificationsAndGoBack(this)
    }
}
