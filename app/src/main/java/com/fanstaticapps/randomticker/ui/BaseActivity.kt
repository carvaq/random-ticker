package com.fanstaticapps.randomticker.ui

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by carvaq
 * Date: 20/09/2017
 * Project: RandomTicker
 */

abstract class BaseActivity : AppCompatActivity() {

    protected fun toast(@StringRes resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

}
