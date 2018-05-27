package com.cvv.fanstaticapps.randomticker.activities

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Created by Carla
 * Date: 05/02/2018
 * Project: RandomTicker
 */
@RunWith(RobolectricTestRunner::class)
abstract class RobolectricBaseTest<A : Activity> internal constructor(private val activityClass: Class<A>) {

    lateinit var activity: Activity

    @Before
    @Throws(Exception::class)
    fun setUp() {
        startActivity()
    }

    private fun startActivity() {
        activity = Robolectric.setupActivity(activityClass)
    }

    internal fun <V : View> getView(@IdRes res: Int): V {
        return activity.findViewById(res)
    }
}