package com.cvv.fanstaticapps.randomticker.helper

import android.widget.SeekBar
import android.widget.TextView
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.robolectric.RobolectricTestRunner

/**
 * Created by carvaq
 * Date: 05/02/2018
 * Project: RandomTicker
 */

@RunWith(RobolectricTestRunner::class)
class TimeSeekBarChangeListenerTest {

    @get:Rule
    var rule = MockitoJUnit.rule()

    @Mock
    private val seekBarMinutes: SeekBar = mock()
    @Mock
    private val seekBarSeconds: SeekBar = mock()
    @Mock
    private val displayTextView: TextView = mock()

    private var timeSeekBarChangeListener: TimeSeekBarChangeListener? = null

    @Before
    fun setUp() {
        timeSeekBarChangeListener = TimeSeekBarChangeListener(displayTextView, seekBarMinutes, seekBarSeconds)
    }

    @Test
    fun testCorrectFormat() {
        setSeekBarValues(12, 2)
        verify<TextView>(displayTextView).text = "12m 02s"

        setSeekBarValues(2, 2)
        verify<TextView>(displayTextView).text = "02m 02s"

        setSeekBarValues(12, 12)
        verify<TextView>(displayTextView).text = "12m 12s"
    }

    private fun setSeekBarValues(minutes: Int, seconds: Int) {
        whenever(seekBarMinutes.progress).thenReturn(minutes)
        whenever(seekBarSeconds.progress).thenReturn(seconds)
        timeSeekBarChangeListener!!.onProgressChanged(seekBarMinutes, 0, true)
    }
}