package com.fanstaticapps.randomticker.ui.klaxon

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.EXTRA_BOOKMARK_ID
import com.fanstaticapps.randomticker.extensions.requireBookmarkId
import com.fanstaticapps.randomticker.extensions.turnScreenOffAndKeyguardOn
import com.fanstaticapps.randomticker.extensions.turnScreenOnAndKeyguardOff
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class KlaxonActivity : BaseActivity() {

    private val klaxonViewModel: KlaxonViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        val bookmarkId = intent.requireBookmarkId()
        setContent {
            AppTheme {
                klaxonViewModel.getCurrentBookmark(bookmarkId).collectAsState(null).value?.let {
                    KlaxonView(it) { openMainActivity(it) }
                }
            }
        }
        klaxonViewModel.needsRestartDueToAutoRepeat(bookmarkId).observe(this) {
            openMainActivity(it)
        }
        turnScreenOnAndKeyguardOff()
    }

    private fun openMainActivity(bookmark: Bookmark) {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(EXTRA_BOOKMARK_ID, bookmark.id)
        })
        noOpenOrCloseTransitions()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }
}