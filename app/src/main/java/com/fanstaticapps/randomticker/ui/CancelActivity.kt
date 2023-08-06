package com.fanstaticapps.randomticker.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import com.fanstaticapps.randomticker.helper.IntentHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelActivity : BaseActivity() {
    private val cancelBookmarkViewModel: CancelBookmarkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bookmarkId = intent.getBookmarkId()
        cancelBookmarkViewModel.cancelTimer(bookmarkId)
        startActivity(IntentHelper.getMainActivity(this, bookmarkId))
        noOpenOrCloseTransitions()
        finish()
    }
}