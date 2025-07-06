package com.fanstaticapps.randomticker.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.helper.MigrationService
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.main.compose.RandomTimerAppContent
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private val migrationService: MigrationService by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        migrationService.runMigrationIfNeeded()
        setContent {
            AppTheme {
                val windowWidthSizeClass = calculateWindowSizeClass(activity = this).widthSizeClass
                RandomTimerAppContent(windowWidthSizeClass, mainViewModel)

            }
        }
    }


}