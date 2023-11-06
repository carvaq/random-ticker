package com.fanstaticapps.randomticker.ui.main.compose

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun CountUp(now: Long, content: @Composable (remainingTime: String) -> Unit) {
    var remainingTime by remember(now) {
        mutableLongStateOf(System.currentTimeMillis() - now)
    }

    content.invoke(DateUtils.formatElapsedTime(remainingTime / 1_000))

    LaunchedEffect(remainingTime) {
        delay(1_000L)
        remainingTime = System.currentTimeMillis() - now
    }
}