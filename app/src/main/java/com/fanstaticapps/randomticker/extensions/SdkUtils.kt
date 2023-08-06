package com.fanstaticapps.randomticker.extensions

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
fun isAtLeastO_MR1() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isAtLeastS() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isAtLeastT() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

/*
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun isAtLeastU() =  Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
*/