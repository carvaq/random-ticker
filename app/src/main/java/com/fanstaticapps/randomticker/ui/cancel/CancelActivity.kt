package com.fanstaticapps.randomticker.ui.cancel

import android.os.Bundle
import com.fanstaticapps.randomticker.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class CancelActivity : BaseActivity() {

    private val cancelViewModel: CancelViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelViewModel.cancelTicker(this)
        noOpenOrCloseTransitions()
        finish()
    }
}