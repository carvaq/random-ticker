package com.fanstaticapps.randomticker.helper

import android.content.Context
import android.content.Intent
import com.fanstaticapps.common.activities.KlaxonBaseActivity
import com.fanstaticapps.common.helper.IntentHelper
import com.fanstaticapps.randomticker.ui.KlaxonActivity
import com.fanstaticapps.randomticker.ui.MainActivity
import com.fanstaticapps.randomticker.receiver.OnAlarmReceiver
import javax.inject.Inject

class RandomTickerIntentHelper @Inject constructor() : IntentHelper {
    override fun getMainActivity(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }

    override fun getAlarmReceiver(context: Context): Intent {
        return Intent(context, OnAlarmReceiver::class.java)
    }

    override fun getKlaxonActivity(context: Context, hasTimeElapsed: Boolean): Intent {
        val intent = Intent(context, KlaxonActivity::class.java)
        intent.putExtra(KlaxonBaseActivity.EXTRA_TIME_ELAPSED, hasTimeElapsed)
        return intent
    }
}