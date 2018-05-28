package com.cvv.fanstaticapps.randomticker.activities

import android.content.Intent
import android.view.View
import android.widget.TextView
import com.cvv.fanstaticapps.randomticker.R
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.Test
import org.robolectric.shadows.ShadowApplication

/**
 * Created by carvaq
 * Date: 05/02/2018
 * Project: RandomTicker
 */
class MainActivityTest : RobolectricBaseTest<MainActivity>(MainActivity::class.java) {

    @Test
    fun `max values are smaller than min values`() {
        setValues(11, 4, 0, 0)

        clickStartButton()

        val actual = ShadowApplication.getInstance().nextStartedActivity
        assertNull(actual)
    }

    @Test
    fun `max values are bigger than min values and don't overflow`() {
        setValues(2, 3, 480, 450)

        val maxMinTextView = getView<TextView>(R.id.maxMin)
        val maxSecTextView = getView<TextView>(R.id.maxSec)

        assertEquals("240", maxMinTextView.text.toString())
        assertEquals("59", maxSecTextView.text.toString())

        clickStartButton()

        val expectedIntent = Intent(activity, KlaxonActivity::class.java)
        val actual = ShadowApplication.getInstance().nextStartedActivity
        assertEquals(expectedIntent.component, actual.component)

    }

    private fun clickStartButton() {
        getView<View>(R.id.start).performClick()
    }

    private fun setValues(minMinutes: Int, minSeconds: Int, maxMinutes: Int, maxSeconds: Int) {
        val minMinTextView = getView<TextView>(R.id.minMin)
        val minSecTextView = getView<TextView>(R.id.minSec)
        val maxMinTextView = getView<TextView>(R.id.maxMin)
        val maxSecTextView = getView<TextView>(R.id.maxSec)
        minMinTextView.text = minMinutes.toString()
        minSecTextView.text = minSeconds.toString()
        maxMinTextView.text = maxMinutes.toString()
        maxSecTextView.text = maxSeconds.toString()

    }

}