package com.fanstaticapps.randomticker.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.NumberPicker
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.IntervalDefinition
import com.fanstaticapps.randomticker.extensions.nonNull
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.TimerHelper
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
            prepareValueSelectionView(minHours, maximumMinutes, 10)
            prepareValueSelectionView(minMin, minimumMinutes, 59)
            prepareValueSelectionView(minSec, minimumSeconds, 59)
            prepareValueSelectionView(maxHours, maximumMinutes, 10)
            prepareValueSelectionView(maxMin, maximumMinutes, 59)
            prepareValueSelectionView(maxSec, maximumSeconds, 59)
        }
    }

    override fun onBookmarkSelected(bookmark: Bookmark) {
        viewModel.setCurrentBookmark(bookmark)
    }

    private fun prepareValueSelectionView(view: NumberPicker, startValue: Int, maxValue: Int) {
        view.value = startValue
        view.minValue = 0
        view.maxValue = maxValue
    }

    private fun createTimer() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SET_ALARM) == PackageManager.PERMISSION_GRANTED) {
            viewModel.createTimer(etBookmarkName.name(),
                    IntervalDefinition(minHours.value, minMin.value, minSec.value),
                    IntervalDefinition(maxHours.value, maxMin.value, maxSec.value)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(android.Manifest.permission.SET_ALARM), 1)
        }
    }

    private fun startAlarmActivity() {
        startActivity(intentHelper.getKlaxonActivity(this, false))
        finish()
    }

    private fun EditText.name(): String = if (this.text.isNullOrBlank()) "Random Ticker" else this.text.toString()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            createTimer()
        }
    }
}
