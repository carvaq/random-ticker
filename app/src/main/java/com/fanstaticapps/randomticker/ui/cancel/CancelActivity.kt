package com.fanstaticapps.randomticker.ui.cancel

import android.os.Bundle
import androidx.activity.viewModels
import com.fanstaticapps.randomticker.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelActivity : BaseActivity() {

    private val viewModel: CancelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelTimer(this)
        noOpenOrCloseTransitions()
        finish()
    }
}