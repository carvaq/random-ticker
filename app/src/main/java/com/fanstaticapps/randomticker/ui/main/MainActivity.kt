package com.fanstaticapps.randomticker.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.NumberPicker
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.IntervalDefinition
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TimerHelper
import com.fanstaticapps.randomticker.helper.livedata.nonNull
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.bookmarks.BookmarkDialog
import com.fanstaticapps.randomticker.ui.preferences.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.content_bookmarks.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_maximum_value.*
import kotlinx.android.synthetic.main.content_minimum_value.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var timerHelper: TimerHelper

    @Inject
    lateinit var userPreferences: UserPreferences

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (timerHelper.isCurrentlyTickerRunning()) {
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
        btnStartTicker.setOnClickListener { createTimer() }
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
        minHours.init(10)
        minMin.init(59)
        minSec.init(59)
        maxHours.init(10)
        maxMin.init(59)
        maxSec.init(59)
    }


    private fun renderBookmark(bookmark: Bookmark) {
        with(bookmark) {
            etBookmarkName.setText(name)
            minHours.value = minimumHours
            minMin.value = minimumMinutes
            minSec.value = minimumSeconds
            maxHours.value = maximumHours
            maxMin.value = maximumMinutes
            maxSec.value = maximumSeconds
        }
    }

    private fun createTimer() {
        viewModel.createTimer(etBookmarkName.name(),
                IntervalDefinition(minHours.value, minMin.value, minSec.value),
                IntervalDefinition(maxHours.value, maxMin.value, maxSec.value),
                cbAutoRepeat.isChecked
        )
    }

    private fun startAlarmActivity() {
        startActivity(IntentHelper.getKlaxonActivity(this, false))
        finish()
    }

    private fun EditText.name(): String = if (this.text.isNullOrBlank()) "Random Ticker" else this.text.toString()
    private fun NumberPicker.init(max: Int) {
        minValue = 0
        maxValue = max
    }
}
