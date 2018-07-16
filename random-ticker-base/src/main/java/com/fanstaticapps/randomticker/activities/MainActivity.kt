package com.fanstaticapps.randomticker.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.fanstaticapps.common.TimerHelper
import com.fanstaticapps.common.activities.BaseActivity
import com.fanstaticapps.common.activities.SettingsActivity
import com.fanstaticapps.common.helper.IntentHelper
import com.fanstaticapps.common.view.MaxValueTextWatcher
import com.fanstaticapps.common.view.MinValueVerification
import com.fanstaticapps.randomticker.PREFS
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.TickerDatabase
import com.fanstaticapps.randomticker.mvp.MainPresenter
import com.fanstaticapps.randomticker.mvp.MainView
import kotlinx.android.synthetic.main.content_bookmarks_row1.*
import kotlinx.android.synthetic.main.content_bookmarks_row2.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_maximum_value.*
import kotlinx.android.synthetic.main.content_minimum_value.*
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView {

    @Inject
    lateinit var timerHelper: TimerHelper
    @Inject
    lateinit var intentHelper: IntentHelper

    private lateinit var presenter: MainPresenter
    private lateinit var bookmarkViews: List<View>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PREFS.currentlyTickerRunning) {
            // if the timer is running we should move the KlaxonActivity
            startAlarmActivity()
        } else {
            setContentView(R.layout.activity_main)
            bookmarkViews = listOf<View>(bookmark1, bookmark2, bookmark3, bookmark4, bookmark5, bookmark6, bookmark7, bookmark8)
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            val database = TickerDatabase.getInstance(this)!!

            presenter = MainPresenter(database, this)
            addDisposable(presenter.loadDataFromDatabase(PREFS.currentSelectedPosition))
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

    override fun initializeBookmarks(initialSelectedBookmark: Int) {
        bookmark1.isActivated = true
        val clickListener = View.OnClickListener {
            var selectedPosition = 0
            bookmarkViews.forEachIndexed { index, view ->
                if (view.id == it.id) {
                    selectedPosition = index
                }
            }
            presenter.selectBookmark(selectedPosition)
        }
        for (bookmark in bookmarkViews) {
            bookmark.setOnClickListener(clickListener)
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

    override fun applyTickerData(minimumMinutes: Int, minimumSeconds: Int, maximumMinutes: Int, maximumSeconds: Int, forceDefaultValue: Boolean, selectedPosition: Int) {
        bookmarkViews.forEachIndexed { index, view -> view.isActivated = index == selectedPosition }
        prepareValueSelectionView(minMin, minimumMinutes, 240, forceDefaultValue)
        prepareValueSelectionView(minSec, minimumSeconds, 59, forceDefaultValue)
        prepareValueSelectionView(maxMin, maximumMinutes, 240, forceDefaultValue)
        prepareValueSelectionView(maxSec, maximumSeconds, 59, forceDefaultValue)
    }


    private fun prepareValueSelectionView(view: EditText, startValue: Int, maxValue: Int, forceDefaultValue: Boolean) {
        if (view.text.isNullOrBlank() || forceDefaultValue) {
            view.setText(startValue.toString())
        }

        view.addTextChangedListener(MaxValueTextWatcher(view, maxValue))
        view.onFocusChangeListener = MinValueVerification()
    }

    private fun createTimer() {
        presenter.createTimer(minMin.getTextAsInt(), minSec.getTextAsInt(), maxMin.getTextAsInt(), maxSec.getTextAsInt())
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
}
