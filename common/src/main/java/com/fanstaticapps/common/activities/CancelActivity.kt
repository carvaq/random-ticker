package com.fanstaticapps.common.activities

import android.os.Bundle
import com.fanstaticapps.common.TimerHelper
import com.fanstaticapps.common.UserPreferences
import javax.inject.Inject

class CancelActivity : BaseActivity() {
    @Inject
    lateinit var timerHelper: TimerHelper
    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timerHelper.cancelNotificationAndGoBack(this, userPreferences)
    }
}
