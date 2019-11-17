package com.fanstaticapps.randomticker.helper

import android.content.Context
import android.content.Intent
import com.fanstaticapps.randomticker.receiver.OnAlarmReceiver
import com.fanstaticapps.randomticker.ui.ticker.KlaxonActivity
import com.fanstaticapps.randomticker.ui.ticker.MainActivity
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
        intent.putExtra(KlaxonActivity.EXTRA_TIME_ELAPSED, hasTimeElapsed)
        return intent
    }
}