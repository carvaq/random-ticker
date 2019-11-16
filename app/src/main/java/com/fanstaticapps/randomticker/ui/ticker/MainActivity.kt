package com.fanstaticapps.randomticker.ui.ticker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TimerHelper
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.TickerDatabase
import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.mvp.MainPresenter
import com.fanstaticapps.randomticker.mvp.MainView
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.preferences.SettingsActivity
import com.fanstaticapps.randomticker.view.MaxValueTextWatcher
import com.fanstaticapps.randomticker.view.MinValueVerification
import kotlinx.android.synthetic.main.content_bookmarks.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_maximum_value.*
import kotlinx.android.synthetic.main.content_minimum_value.*
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView, BookmarkDialog.BookmarkSelector {

    @Inject
    lateinit var timerHelper: TimerHelper
    @Inject
    lateinit var intentHelper: IntentHelper

    private lateinit var presenter: MainPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PREFS.currentlyTickerRunning) {
            // if the timer is running we should move the KlaxonActivity
            startAlarmActivity()
        } else {
            setContentView(R.layout.activity_main)
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            val database = TickerDatabase.getInstance(this)!!

            presenter = MainPresenter(database, this)
            addDisposable(presenter.loadDataFromDatabase(PREFS.currentSelectedId))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        TickerDatabase.destroyInstance()
        super.onDestroy()
    }

    override fun initializeBookmarks() {
        btnSelectProfile.setOnClickListener {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                BookmarkDialog().show(supportFragmentManager, "BookmarkSelector")
            }
        }
    }

    override fun initializeListeners() {
        maxSec.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> createTimer()
            }
            true
        }
        start.setOnClickListener { createTimer() }
    }

    override fun applyTickerData(minimumMinutes: Int, minimumSeconds: Int, maximumMinutes: Int, maximumSeconds: Int, forceDefaultValue: Boolean, name: String) {
        etBookmarkName.setText(name)
        prepareValueSelectionView(minMin, minimumMinutes, 240, forceDefaultValue)
        prepareValueSelectionView(minSec, minimumSeconds, 59, forceDefaultValue)
        prepareValueSelectionView(maxMin, maximumMinutes, 240, forceDefaultValue)
        prepareValueSelectionView(maxSec, maximumSeconds, 59, forceDefaultValue)
    }

    override fun onBookmarkSelected(bookmark: Bookmark?) {
        presenter.selectBookmark(bookmark)
    }

    private fun prepareValueSelectionView(view: EditText, startValue: Int, maxValue: Int, forceDefaultValue: Boolean) {
        if (view.text.isNullOrBlank() || forceDefaultValue) {
            view.setText(startValue.toString())
        }

        view.addTextChangedListener(MaxValueTextWatcher(view, maxValue))
        view.onFocusChangeListener = MinValueVerification()
    }

    private fun createTimer() {
        presenter.createTimer(etBookmarkName.name(), minMin.getTextAsInt(), minSec.getTextAsInt(), maxMin.getTextAsInt(), maxSec.getTextAsInt())
    }

    override fun showMinimumMustBeBiggerThanMaximum() {
        toast(R.string.error_min_is_bigger_than_max)
    }

    override fun createAlarm() {
        timerHelper.createNotificationAndAlarm(this)
        startAlarmActivity()
    }

    private fun startAlarmActivity() {
        startActivity(intentHelper.getKlaxonActivity(this, false))
        finish()
    }

    private fun EditText.getTextAsInt(): Int = if (this.text.isNullOrBlank()) 0 else this.text.toString().toInt()
    private fun EditText.name(): String = if (this.text.isNullOrBlank()) "Random Ticker" else this.text.toString()
}
