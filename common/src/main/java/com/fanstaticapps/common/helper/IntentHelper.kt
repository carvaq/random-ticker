package com.fanstaticapps.common.helper

import android.content.Context
import android.content.Intent

interface IntentHelper {
    fun getKlaxonActivity(context: Context, hasTimeElapsed: Boolean): Intent
    fun getMainActivity(context: Context): Intent
    fun getAlarmReceiver(context: Context): Intent
}