package com.cvv.fanstaticapps.randomticker.helper

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.robolectric.RobolectricTestRunner

/**
 * Created by carvaq
 * Date: 05/02/2018
 * Project: RandomTicker
 */

@RunWith(RobolectricTestRunner::class)
class MaxValueTextWatcherTest {

    @get:Rule
    var rule = MockitoJUnit.rule()

    @Mock
    private val editText: EditText = mock()

    private lateinit var maxValueTextWatcher: MaxValueTextWatcher
    private var MAX_VALUE_IN_EDITTEXT = "13"

    @Before
    fun setUp() {
        maxValueTextWatcher = MaxValueTextWatcher(editText, 13)
    }

    @Test
    fun `Value is never bigger than constraint`() {
        mockTextChanged("1")
        verify(editText, never()).text = any()
        mockTextChanged(null)
        verify(editText, never()).text = any()
        mockTextChanged("13")
        verify(editText).setText(MAX_VALUE_IN_EDITTEXT)
        mockTextChanged("14")
        //The second time is because it was called once already in this test
        verify(editText, times(2)).setText(MAX_VALUE_IN_EDITTEXT)
    }

    private fun mockTextChanged(text: String?) {
        val editable: Editable? = if (text == null) {
            null
        } else {
            Editable.Factory.getInstance().newEditable(text)
        }
        whenever(editText.text).thenReturn(editable)
        maxValueTextWatcher.onTextChanged(null, 0, 0, 2)
    }
}

