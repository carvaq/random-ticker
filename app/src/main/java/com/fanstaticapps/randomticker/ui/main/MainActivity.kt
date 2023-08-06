package com.fanstaticapps.randomticker.ui.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.IntervalDefinition
import com.fanstaticapps.randomticker.databinding.ActivityMainBinding
import com.fanstaticapps.randomticker.databinding.ContentMainBinding
import com.fanstaticapps.randomticker.extensions.getAlarmManager
import com.fanstaticapps.randomticker.extensions.isAtLeastS
import com.fanstaticapps.randomticker.extensions.isAtLeastT
import com.fanstaticapps.randomticker.extensions.viewBinding
import com.fanstaticapps.randomticker.helper.Migration
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.bookmarks.BookmarkDialog
import com.fanstaticapps.randomticker.ui.preferences.SettingsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {


    @Inject
    lateinit var migrator: Migration

    private val viewModel: MainViewModel by viewModels()
    private val viewBinding by viewBinding(ActivityMainBinding::inflate)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) createTimerIfScheduleAlarmGranted()
        }
    private val scheduleAlarmLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            createTimerIfScheduleAlarmGranted()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        migrator.migrate()

        setContentView(viewBinding.root)

        setupToolbar()

        initializeBookmarks()
        viewBinding.content.initializeTimerCreationStatus()
        viewBinding.content.btnStartTicker.setOnClickListener { onStartClicked() }
    }

    private fun setupToolbar() {
        setSupportActionBar(viewBinding.appbar.toolbar)
        viewBinding.appbar.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }


    private fun initializeBookmarks() {
        viewModel.currentBookmark.observe(this) {
            viewBinding.content.renderBookmark(it)
        }
        viewBinding.content.bookmarks.btnSelectBookmark.setOnClickListener {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                BookmarkDialog().show(supportFragmentManager, "BookmarkSelector")
            }
        }
    }

    private fun ContentMainBinding.initializeTimerCreationStatus() {
        contentMin.minHours.init(10, R.string.from, R.string.hours_label)
        contentMin.minMin.init(59, R.string.from, R.string.minutes_label)
        contentMin.minSec.init(59, R.string.from, R.string.seconds_label)
        contentMax.maxHours.init(23, R.string.to, R.string.hours_label)
        contentMax.maxMin.init(59, R.string.to, R.string.minutes_label)
        contentMax.maxSec.init(59, R.string.to, R.string.seconds_label)
    }


    private fun ContentMainBinding.renderBookmark(bookmark: Bookmark) = with(bookmark) {
        bookmarks.etBookmarkName.setText(name)
        contentMin.minHours.value = minimumHours
        contentMin.minMin.value = minimumMinutes
        contentMin.minSec.value = minimumSeconds
        contentMax.maxHours.value = maximumHours
        contentMax.maxMin.value = maximumMinutes
        contentMax.maxSec.value = maximumSeconds
        cbAutoRepeat.isChecked = autoRepeat
        viewBinding.content.btnStartTicker.prepareListener(bookmark)
    }

    private fun Button.prepareListener(bookmark: Bookmark) {
        val timerRunning = bookmark.intervalEnd > System.currentTimeMillis()
        setText(if (timerRunning) R.string.stop_button else R.string.start_button)
        setOnClickListener {
            if (timerRunning) viewModel.cancelTimer(this@MainActivity, bookmark)
            else onStartClicked()
        }
    }

    private fun onStartClicked() {
        if (isAtLeastT()) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            createTimerIfScheduleAlarmGranted()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestScheduleAlarmPermission() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(R.string.please_allow_alarm_scheduling)
            setPositiveButton(android.R.string.ok) { _, _ ->
                scheduleAlarmLauncher.launch(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
            setPositiveButton(android.R.string.cancel, null)
        }
    }


    private fun createTimerIfScheduleAlarmGranted() {
        if (!isAtLeastS() || getAlarmManager()?.canScheduleExactAlarms() == true) {
            val minimum = IntervalDefinition(
                viewBinding.content.contentMin.minHours.value,
                viewBinding.content.contentMin.minMin.value,
                viewBinding.content.contentMin.minSec.value
            )
            val maximum = IntervalDefinition(
                viewBinding.content.contentMax.maxHours.value,
                viewBinding.content.contentMax.maxMin.value,
                viewBinding.content.contentMax.maxSec.value
            )
            if (minimum < maximum) {
                viewModel.createTimer(
                    this,
                    viewBinding.content.bookmarks.etBookmarkName.name(),
                    minimum,
                    maximum,
                    viewBinding.content.cbAutoRepeat.isChecked
                )
            } else {
                toast(R.string.error_min_is_bigger_than_max)
            }
        } else {
            requestScheduleAlarmPermission()
        }
    }

    private fun EditText.name(): String =
        if (this.text.isNullOrBlank()) "Random Ticker" else this.text.toString()

    private fun NumberPicker.init(max: Int, @StringRes fromToResId: Int, @StringRes type: Int) {
        minValue = 0
        maxValue = max
        contentDescription = "${getString(fromToResId)} ${getString(type)}"
    }
}