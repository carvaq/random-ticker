package com.cvv.fanstaticapps.randomticker.helper

import android.widget.EditText
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
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

    @Before
    fun setUp() {
        maxValueTextWatcher = MaxValueTextWatcher(editText, 13)
    }

    @Test
    fun `Value is never bigger than constraint`() {
        maxValueTextWatcher.onTextChanged("12", 0, 0, 2)
        verify(editText, never()).setText(anyString())
    }

}