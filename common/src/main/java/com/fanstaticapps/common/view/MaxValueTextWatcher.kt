package com.fanstaticapps.common.view

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Created by carvaq
 * Date: 04/02/2018
 * Project: RandomTicker
 */

class MaxValueTextWatcher(private val editText: EditText, private val maxValue: Int) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = editText.text
        if (!text.isNullOrEmpty()) {
            val interval = text.toString().toInt()
            if (interval >= maxValue) {
                editText.removeTextChangedListener(this)
                editText.setText(maxValue.toString())
                editText.addTextChangedListener(this)
            }
        }
    }
}
