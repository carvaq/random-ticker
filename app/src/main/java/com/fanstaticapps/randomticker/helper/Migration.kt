package com.fanstaticapps.randomticker.helper

import android.content.Context
import com.fanstaticapps.randomticker.TickerPreferences
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class Migration @Inject constructor(
    private val tickerPreferences: TickerPreferences,
    @ActivityContext private val context: Context
) {


    fun migrate() {
    }

    companion object {
        const val OLD_RUNNING_CHANNEL_ID = "RandomTickerChannel:01"

    }
}
