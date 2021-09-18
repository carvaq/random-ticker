package com.fanstaticapps.randomticker.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.NumberPicker
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.IntervalDefinition
import com.fanstaticapps.randomticker.databinding.ActivityMainBinding
import com.fanstaticapps.randomticker.extensions.viewBinding
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TimerHelper
import com.fanstaticapps.randomticker.helper.livedata.nonNull
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.bookmarks.BookmarkDialog
import com.fanstaticapps.randomticker.ui.preferences.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var timerHelper: TimerHelper

    @Inject
    lateinit var tickerPreferences: TickerPreferences

    private val viewModel: MainViewModel by viewModels()

    private val viewBinding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (timerHelper.isCurrentlyTickerRunning()) {
            // if the timer is running we should move the KlaxonActivity
            startAlarmActivity()
        } else {
            setContentView(viewBinding.root)

            setSupportActionBar(viewBinding.appbar.toolbar)

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
        viewBinding.content.bookmarks.btnSelectBookmark.setOnClickListener {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                BookmarkDialog().show(supportFragmentManager, "BookmarkSelector")
            }
        }
    }

    private fun initializeListeners() {
        viewBinding.content.btnStartTicker.setOnClickListener { createTimer() }
    }

    private fun initializeTimerCreationStatus() {
        viewModel.timerCreationStatus.observe(this) {
            when (it) {
                TimerCreationStatus.TIMER_STARTED -> {
                    timerHelper.startTicker(this)
                    startAlarmActivity()
                }
                TimerCreationStatus.INVALID_DATES -> {
                    toast(R.string.error_min_is_bigger_than_max)
                }
                else -> {
                }
            }
        }
        viewBinding.content.contentMin.minHours.init(10, R.string.from, R.string.hours_label)
        viewBinding.content.contentMin.minMin.init(59, R.string.from, R.string.minutes_label)
        viewBinding.content.contentMin.minSec.init(59, R.string.from, R.string.seconds_label)
        viewBinding.content.contentMax.maxHours.init(10, R.string.to, R.string.hours_label)
        viewBinding.content.contentMax.maxMin.init(59, R.string.to, R.string.minutes_label)
        viewBinding.content.contentMax.maxSec.init(59, R.string.to, R.string.seconds_label)
    }


    private fun renderBookmark(bookmark: Bookmark) {
        with(bookmark) {
            viewBinding.content.bookmarks.etBookmarkName.setText(name)
            viewBinding.content.contentMin.minHours.value = minimumHours
            viewBinding.content.contentMin.minMin.value = minimumMinutes
            viewBinding.content.contentMin.minSec.value = minimumSeconds
            viewBinding.content.contentMax.maxHours.value = maximumHours
            viewBinding.content.contentMax.maxMin.value = maximumMinutes
            viewBinding.content.contentMax.maxSec.value = maximumSeconds
            viewBinding.content.cbAutoRepeat.isChecked = bookmark.autoRepeat
        }
    }

    private fun createTimer() {
        viewModel.createTimer(
            viewBinding.content.bookmarks.etBookmarkName.name(),
            IntervalDefinition(
                viewBinding.content.contentMin.minHours.value,
                viewBinding.content.contentMin.minMin.value,
                viewBinding.content.contentMin.minSec.value
            ),
            IntervalDefinition(
                viewBinding.content.contentMax.maxHours.value,
                viewBinding.content.contentMax.maxMin.value,
                viewBinding.content.contentMax.maxSec.value
            ),
            viewBinding.content.cbAutoRepeat.isChecked
        )
    }

    private fun startAlarmActivity() {
        startActivity(IntentHelper.getKlaxonActivity(this, false))
        finish()
    }

    private fun EditText.name(): String =
        if (this.text.isNullOrBlank()) "Random Ticker" else this.text.toString()

    private fun NumberPicker.init(max: Int, @StringRes fromToResId: Int, @StringRes type: Int) {
        minValue = 0
        maxValue = max
        contentDescription = "${getString(fromToResId)} ${getString(type)}"
    }
}
