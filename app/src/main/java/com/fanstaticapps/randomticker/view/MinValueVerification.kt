package com.fanstaticapps.randomticker.view

import android.view.View
import android.widget.EditText

/**
 * Created by carvaq
 * Date: 5/28/18
 * Project: random-ticker
 */
class MinValueVerification : View.OnFocusChangeListener {
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus && v is EditText && v.text.isNullOrBlank()) {
            v.setText("0")
        }
    }
}