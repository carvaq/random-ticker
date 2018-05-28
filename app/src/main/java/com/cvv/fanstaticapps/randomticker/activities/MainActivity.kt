package com.cvv.fanstaticapps.randomticker.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.cvv.fanstaticapps.randomticker.R
import com.cvv.fanstaticapps.randomticker.helper.TimeSeekBarChangeListener
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper
import com.cvv.fanstaticapps.randomticker.prefs
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_maximum_value.*
import kotlinx.android.synthetic.main.content_minimum_value.*
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var timerHelper: TimerHelper

    private val randomGenerator = Random(System.currentTimeMillis())
    private var minValuesSeekBarListener: TimeSeekBarChangeListener? = null
    private var maxValuesSeekBarListener: TimeSeekBarChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (prefs.currentlyTickerRunning) {
            // if the timer is running we should move the KlaxonActivity
            startAlarmActivity()
        } else {
            setContentView(R.layout.activity_main)
            prepareView()
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


    private fun prepareView() {
        setSupportActionBar(toolbar)


        minValuesSeekBarListener = TimeSeekBarChangeListener(minValue, minMin, minSec)
        maxValuesSeekBarListener = TimeSeekBarChangeListener(maxValue, maxMin, maxSec)

        prepareValueSelectionView(minMin, prefs.minMin, minValuesSeekBarListener!!)
        prepareValueSelectionView(minSec, prefs.minSec, minValuesSeekBarListener!!)
        prepareValueSelectionView(maxMin, prefs.maxMin, maxValuesSeekBarListener!!)
        prepareValueSelectionView(maxSec, prefs.maxSec, maxValuesSeekBarListener!!)

        start.setOnClickListener({ createTimer() })
    }

    private fun prepareValueSelectionView(seekBar: SeekBar, startValue: Int,
                                          seekBarChangeListener: TimeSeekBarChangeListener) {
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBar.max = 59

        //This is for the configuration change case
        if (seekBar.progress == 0) {
            seekBar.progress = startValue
        }
    }

    private fun startAlarmActivity() {
        startActivity(KlaxonActivityNavigator(false).build(this))
        finish()
    }

    private fun createTimer() {
        val min = getTotalValueInMillis(minValuesSeekBarListener!!)
        val max = getTotalValueInMillis(maxValuesSeekBarListener!!)
        if (max > min) {
            val interval = (randomGenerator.nextInt(max - min + 1) + min).toLong()
            val intervalFinished = System.currentTimeMillis() + interval
            saveToPreferences(interval, intervalFinished)
            timerHelper.createNotificationAndAlarm(this, prefs)
            startAlarmActivity()
        } else {
            toast(R.string.error_min_is_bigger_than_max)
        }
    }

    private fun getTotalValueInMillis(seekBarChangeListener: TimeSeekBarChangeListener): Int {
        return (60 * seekBarChangeListener.minutes + seekBarChangeListener.seconds) * 1000
    }

    private fun saveToPreferences(interval: Long, intervalFinished: Long) {
        prefs.maxMin = maxMin!!.progress
        prefs.minMin = minMin!!.progress
        prefs.maxSec = maxSec!!.progress
        prefs.minSec = minSec!!.progress
        prefs.currentlyTickerRunning = true
        prefs.interval = interval
        prefs.intervalFinished = intervalFinished
    }
}
