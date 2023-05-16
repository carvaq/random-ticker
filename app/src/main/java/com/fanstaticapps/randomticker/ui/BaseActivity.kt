package com.fanstaticapps.randomticker.ui

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.fanstaticapps.randomticker.extensions.isAtLeastU

/**
 * Created by carvaq
 * Date: 20/09/2017
 * Project: RandomTicker
 */

abstract class BaseActivity : AppCompatActivity() {

    protected fun toast(@StringRes resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    protected fun noOpenOrCloseTransitions() {
        /*if (isAtLeastU()) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            overridePendingTransition(0, 0)
        }*/
        overridePendingTransition(0, 0)
    }
}