package com.cvv.fanstaticapps.randomticker

import android.view.View
import android.widget.SeekBar
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

/**
 * Created by carvaq
 * Date: 05/02/2018
 * Project: RandomTicker
 */
class SetProgressAction(private val progress: Int) : ViewAction {

    override fun getConstraints(): Matcher<View> {
        return allOf(isDisplayed(), isAssignableFrom(SeekBar::class.java))
    }

    override fun getDescription(): String {
        return "set progress"
    }

    override fun perform(uiController: UiController, view: View) {
        (view as SeekBar).progress = progress
    }

    companion object {

        fun setProgress(progress: Int): SetProgressAction {
            return SetProgressAction(progress)
        }
    }
}
