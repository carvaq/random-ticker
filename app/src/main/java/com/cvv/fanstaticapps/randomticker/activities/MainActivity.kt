package com.cvv.fanstaticapps.randomticker.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.cvv.fanstaticapps.randomticker.PREFS
import com.cvv.fanstaticapps.randomticker.R
import com.cvv.fanstaticapps.randomticker.data.TickerDatabase
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper
import com.cvv.fanstaticapps.randomticker.mvp.MainPresenter
import com.cvv.fanstaticapps.randomticker.mvp.MainView
import com.cvv.fanstaticapps.randomticker.view.MaxValueTextWatcher
import com.cvv.fanstaticapps.randomticker.view.MinValueVerification
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_maximum_value.*
import kotlinx.android.synthetic.main.content_minimum_value.*
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView {

    @Inject
    lateinit var timerHelper: TimerHelper

    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PREFS.currentlyTickerRunning) {
            // if the timer is running we should move the KlaxonActivity
            startAlarmActivity()
        } else {
            setContentView(R.layout.activity_main)
            setSupportActionBar(toolbar)
            val database = TickerDatabase.getInstance(this)!!

            presenter = MainPresenter(database, this)
            presenter.loadDataFromDatabase()
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

    override fun initializeBookmarks(bookmarkNames: List<String>, selectedBookmark: Int) {
        val bookmarksWithAddOption = bookmarkNames.plus(getString(R.string.create_bookmark))
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, bookmarksWithAddOption)
        bookmarks.adapter = aa
        bookmarks.setSelection(selectedBookmark)
        bookmarks.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                presenter.selectBookmark(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        bookmarks.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, view, position, _ ->
            if (position < bookmarkNames.size) {
                showEditDialog(view, position)
            }
            true
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

    override fun applyTickerData(minimumMinutes: Int, minimumSeconds: Int, maximumMinutes: Int, maximumSeconds: Int, forceDefaultValue: Boolean) {
        prepareValueSelectionView(minMin, minimumMinutes, 240, forceDefaultValue)
        prepareValueSelectionView(minSec, minimumSeconds, 59, forceDefaultValue)
        prepareValueSelectionView(maxMin, maximumMinutes, 240, forceDefaultValue)
        prepareValueSelectionView(maxSec, maximumSeconds, 59, forceDefaultValue)
    }

    private fun showEditDialog(view: View?, position: Int) {
        val editTextView = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val editText = editTextView.findViewById<EditText>(R.id.edBookmarkName)
        editText.setText((view as TextView).text)
        val listener = DialogInterface.OnClickListener { _, which ->
            val bookmarkName = editText.text.toString()
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> presenter.saveBookmark(bookmarkName, position)
                DialogInterface.BUTTON_NEGATIVE -> presenter.deleteBookmark(position)
            }
        }
        AlertDialog.Builder(this)
                .setTitle(R.string.edit_bookmark)
                .setView(editTextView)
                .setPositiveButton(R.string.save, listener)
                .setNegativeButton(R.string.delete, listener)
                .show()
    }

    override fun showCreateNewBookmarkDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val editText = view.findViewById<EditText>(R.id.edBookmarkName)
        val listener = DialogInterface.OnClickListener { _, which ->
            val bookmarkName = editText.text.toString()
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> presenter.createBookmark(bookmarkName)
                DialogInterface.BUTTON_NEGATIVE -> {
                    bookmarks.setSelection(0)
                }
            }
        }
        AlertDialog.Builder(this)
                .setTitle(R.string.create_bookmark)
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .show()
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
        timerHelper.createNotificationAndAlarm(this, PREFS)
        startAlarmActivity()
    }

    private fun startAlarmActivity() {
        startActivity(KlaxonActivityNavigator(false).build(this))
        finish()
    }

    private inline fun EditText.getTextAsInt(): Int = if (this.text.isNullOrBlank()) 0 else this.text.toString().toInt()
}
