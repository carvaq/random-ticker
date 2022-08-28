package com.fanstaticapps.randomticker.extensions

import android.os.Build

fun isAtLeastM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun isAtLeastO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun isAtLeastS() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
fun isAtLeastT() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU