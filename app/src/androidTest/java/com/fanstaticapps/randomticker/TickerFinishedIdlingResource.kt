package com.fanstaticapps.randomticker

import androidx.test.espresso.IdlingResource

class TickerFinishedIdlingResource(private val prefs: TickerPreferences) : IdlingResource {

    private var callback: IdlingResource.ResourceCallback? = null
    private val originalInterval = prefs.intervalWillBeFinished

    override fun getName(): String = "SharedPreferencesIdlingResources"

    override fun isIdleNow(): Boolean {
        val intervalFinished = prefs.intervalWillBeFinished
        val isIdle =
            intervalFinished != originalInterval || intervalFinished < System.currentTimeMillis() && intervalFinished > 0
        if (isIdle) {
            callback?.onTransitionToIdle()
        }
        return isIdle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}