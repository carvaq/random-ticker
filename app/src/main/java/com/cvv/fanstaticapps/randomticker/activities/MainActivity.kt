package com.cvv.fanstaticapps.randomticker.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.cvv.fanstaticapps.randomticker.PREFS
import com.cvv.fanstaticapps.randomticker.R
import com.cvv.fanstaticapps.randomticker.data.TickerData
import com.cvv.fanstaticapps.randomticker.data.TickerDatabase
import com.cvv.fanstaticapps.randomticker.helper.IntegerUtil.Companion.getIntegerFromCharSequence
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper
import com.cvv.fanstaticapps.randomticker.view.MaxValueTextWatcher
import com.cvv.fanstaticapps.randomticker.view.MinValueVerification
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_maximum_value.*
import kotlinx.android.synthetic.main.content_minimum_value.*
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var timerHelper: TimerHelper
    private lateinit var database: TickerDatabase

    private lateinit var currentTicker: TickerData
    private lateinit var databaseTickerList: List<TickerData>

    private val randomGenerator = Random(System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PREFS.currentlyTickerRunning) {
            // if the timer is running we should move the KlaxonActivity
            startAlarmActivity()
        } else {
            setContentView(R.layout.activity_main)
            loadDataFromDb()
        }
    }

    private fun loadDataFromDb() {
        database = TickerDatabase.getInstance(this)!!
        database.tickerDataDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    databaseTickerList = it
                    currentTicker = if (it.isNotEmpty()) databaseTickerList[0] else TickerData()
                    prepareView()
                }, {
                    currentTicker = TickerData()
                    prepareView()
                })

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

    private fun prepareView() {
        setSupportActionBar(toolbar)

        prepareValueSelectionView(minMin, currentTicker.minimumMinutes, MaxValueTextWatcher(minMin, 240))
        prepareValueSelectionView(minSec, currentTicker.minimumSeconds, MaxValueTextWatcher(minSec, 59))
        prepareValueSelectionView(maxMin, currentTicker.maximumMinutes, MaxValueTextWatcher(maxMin, 240))
        prepareValueSelectionView(maxSec, currentTicker.maximumSeconds, MaxValueTextWatcher(maxSec, 59))

        maxSec.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> createTimer()
            }
            true
        }
        start.setOnClickListener({ createTimer() })
    }

    private fun prepareValueSelectionView(view: TextView, startValue: Int,
                                          listener: MaxValueTextWatcher) {
        if (view.text.isNullOrBlank()) {
            view.text = startValue.toString()
        }
        view.addTextChangedListener(listener)
        view.onFocusChangeListener = MinValueVerification()
    }

    private fun startAlarmActivity() {
        startActivity(KlaxonActivityNavigator(false).build(this))
        finish()
    }

    private fun createTimer() {
        val min = getTotalValueInMillis(minMin, minSec)
        val max = getTotalValueInMillis(maxMin, maxSec)
        if (max > min) {
            val interval = (randomGenerator.nextInt(max - min + 1) + min).toLong()
            val intervalFinished = System.currentTimeMillis() + interval
            saveToPreferences(interval, intervalFinished)
            timerHelper.createNotificationAndAlarm(this, PREFS)
            startAlarmActivity()
        } else {
            toast(R.string.error_min_is_bigger_than_max)
        }
    }

    private fun getTotalValueInMillis(minutes: TextView, seconds: TextView): Int {
        return (60 * getIntegerFromCharSequence(minutes.text) + getIntegerFromCharSequence(seconds.text)) * 1000
    }

    private fun saveToPreferences(interval: Long, intervalFinished: Long) {
        currentTicker.maximumMinutes = getIntegerFromCharSequence(maxMin.text)
        currentTicker.minimumMinutes = getIntegerFromCharSequence(minMin.text)
        currentTicker.maximumSeconds = getIntegerFromCharSequence(maxSec.text)
        currentTicker.minimumSeconds = getIntegerFromCharSequence(minSec.text)
        Single.fromCallable({ database.tickerDataDao().insert(currentTicker) })
                .subscribeOn(Schedulers.io())
                .subscribe { _ -> Log.d("DB", "Inserted interval changes") }
        PREFS.currentlyTickerRunning = true
        PREFS.interval = interval
        PREFS.intervalFinished = intervalFinished
    }
}
