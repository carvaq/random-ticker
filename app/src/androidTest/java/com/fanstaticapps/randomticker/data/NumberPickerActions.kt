package com.fanstaticapps.randomticker.data

import android.view.View
import android.widget.NumberPicker
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

class NumberPickerAction(private val value: Int) : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return ViewMatchers.isAssignableFrom(NumberPicker::class.java)
    }

    override fun getDescription(): String {
        return "Set the value of a NumberPicker"
    }

    override fun perform(uiController: UiController?, view: View?) {
        (view as NumberPicker).value = value
    }
}

fun pickValue(value: Int): ViewAction = NumberPickerAction(value)