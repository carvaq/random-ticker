package com.cvv.fanstaticapps.randomticker.activities

import android.content.Intent
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.cvv.fanstaticapps.randomticker.R
import com.cvv.fanstaticapps.randomticker.helper.TimeSeekBarChangeListener
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.Test
import org.robolectric.shadows.ShadowApplication
import java.util.*

/**
 * Created by Carla
 * Date: 05/02/2018
 * Project: RandomTicker
 */
class MainActivityTest : RobolectricBaseTest<MainActivity>(MainActivity::class.java) {

    @Test
    fun testTimer_NotCreatedWithMinBiggerThanMax() {
        setSeekBarValues(11, 4, 0, 0)

        clickStartButton()

        val actual = ShadowApplication.getInstance().nextStartedActivity
        assertNull(actual)
    }

    @Test
    fun testTimer_CreatedWithMinSmallerThanMax() {
        setSeekBarValues(2, 3, 10, 59)

        clickStartButton()

        val expectedIntent = Intent(activity, KlaxonActivity::class.java)
        val actual = ShadowApplication.getInstance().nextStartedActivity
        assertEquals(expectedIntent.component, actual.component)

    }

    private fun clickStartButton() {
        getView<View>(R.id.start).performClick()
    }

    private fun setSeekBarValues(minMinutes: Int, minSeconds: Int, maxMinutes: Int, maxSeconds: Int) {
        val minMinSeekBar = getView<SeekBar>(R.id.minMin)
        val minSecSeekBar = getView<SeekBar>(R.id.minSec)
        val maxMinSeekBar = getView<SeekBar>(R.id.maxMin)
        val maxSecSeekBar = getView<SeekBar>(R.id.maxSec)
        maxMinSeekBar.progress = maxMinutes
        maxSecSeekBar.progress = maxSeconds
        minMinSeekBar.progress = minMinutes
        minSecSeekBar.progress = minSeconds

        val minDisplayText = getView<TextView>(R.id.minValue)
        val maxDisplayText = getView<TextView>(R.id.maxValue)
        val expectedMinValue = formatExpectedValue(minMinutes, minSeconds)
        val expectedMaxValue = formatExpectedValue(maxMinutes, maxSeconds)

        assertEquals(expectedMinValue, minDisplayText.text.toString())
        assertEquals(expectedMaxValue, maxDisplayText.text.toString())
    }

    private fun formatExpectedValue(minutes: Int, seconds: Int): String {
        return String.format(Locale.getDefault(), TimeSeekBarChangeListener.TIME_FORMAT, minutes, seconds)
    }

}