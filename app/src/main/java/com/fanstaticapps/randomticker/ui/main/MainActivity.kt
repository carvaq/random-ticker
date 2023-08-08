package com.fanstaticapps.randomticker.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.main.compose.TickerApp
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = {
                            Text(stringResource(id = R.string.app_name))
                        })
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* ... */ },
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.add_bookmark)
                            )
                        }
                    }) { paddingValues ->
                    val bookmarks =
                        mainViewModel.bookmarks.collectAsState(initial = emptyList()).value
                    TickerApp(
                        paddingValues = paddingValues,
                        bookmarks = bookmarks,
                        edit = {},
                        start = { mainViewModel.startBookmark(this, it) },
                        stop = { mainViewModel.stopBookmark(this, it) },
                        delete = { mainViewModel.deleteBookmark(it) },
                        windowSize = calculateWindowSizeClass(activity = this)
                    )
                }
            }
        }
    }


}