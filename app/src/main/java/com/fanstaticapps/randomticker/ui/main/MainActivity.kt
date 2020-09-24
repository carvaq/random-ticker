package com.fanstaticapps.randomticker.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.nonNull
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TimerHelper
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.bookmarks.BookmarkDialog
import com.fanstaticapps.randomticker.ui.preferences.SettingsActivity
import com.fanstaticapps.randomticker.view.MaxValueTextWatcher
import com.fanstaticapps.randomticker.view.MinValueVerification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.content_bookmarks.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_maximum_value.*
import kotlinx.android.synthetic.main.content_minimum_value.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), BookmarkDialog.BookmarkSelector {

    @Inject
    lateinit var timerHelper: TimerHelper

    @Inject
    lateinit var intentHelper: IntentHelper

    @Inject
    lateinit var userPreferences: UserPreferences

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (userPreferences.currentlyTickerRunning) {
            // if the timer is running we should move the KlaxonActivity
            startAlarmActivity()
        } else {
            setContentView(R.layout.activity_main)

            setSupportActionBar(findViewById(R.id.toolbar))

            initializeListeners()
            initializeBookmarks()
            initializeTimerCreationStatus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


    private fun initializeBookmarks() {
        viewModel.currentBookmark
                .nonNull()
                .observe(this) {
                    renderBookmark(it)
                }
        btnSelectProfile.setOnClickListener {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                BookmarkDialog().show(supportFragmentManager, "BookmarkSelector")
            }
        }
    }

    private fun initializeListeners() {
        maxSec.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                createTimer()
            }
            true
        }
        start.setOnClickListener { createTimer() }
    }

    private fun initializeTimerCreationStatus() {
        viewModel.timerCreationStatus.observe(this) {
            when (it) {
                TimerCreationStatus.TIMER_STARTED -> {
                    timerHelper.createAlarm(this)
                    startAlarmActivity()
                }
                TimerCreationStatus.INVALID_DATES -> {
                    toast(R.string.error_min_is_bigger_than_max)
                }
                else -> {
                }
            }
        }
    }


    private fun renderBookmark(bookmark: Bookmark) {
        with(bookmark) {
            etBookmarkName.setText(name)
            prepareValueSelectionView(minMin, minimumMinutes, 240, true)
            prepareValueSelectionView(minSec, minimumSeconds, 59, true)
            prepareValueSelectionView(maxMin, maximumMinutes, 240, true)
            prepareValueSelectionView(maxSec, maximumSeconds, 59, true)
        }
    }

    override fun onBookmarkSelected(bookmark: Bookmark) {
        viewModel.setCurrentBookmark(bookmark)
    }

    private fun prepareValueSelectionView(view: EditText, startValue: Int, maxValue: Int, forceDefaultValue: Boolean) {
        if (view.text.isNullOrBlank() || forceDefaultValue) {
            view.setText(startValue.toString())
        }

        view.addTextChangedListener(MaxValueTextWatcher(view, maxValue))
        view.onFocusChangeListener = MinValueVerification()
    }

    private fun createTimer() {
        viewModel.createTimer(etBookmarkName.name(),
                minMin.getTextAsInt(),
                minSec.getTextAsInt(),
                maxMin.getTextAsInt(),
                maxSec.getTextAsInt()
        )
    }

    private fun startAlarmActivity() {
        startActivity(intentHelper.getKlaxonActivity(this, false))
        finish()
    }

    private fun EditText.getTextAsInt(): Int = if (this.text.isNullOrBlank()) 0 else this.text.toString().toInt()
    private fun EditText.name(): String = if (this.text.isNullOrBlank()) "Random Ticker" else this.text.toString()
}
