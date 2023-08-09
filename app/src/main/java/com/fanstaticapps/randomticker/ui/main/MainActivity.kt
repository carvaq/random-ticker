package com.fanstaticapps.randomticker.ui.main

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.extensions.needsPostNotificationPermission
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.main.compose.TickerApp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val isSinglePane = isCompactOrInPortrait()
                val bookmarks = mainViewModel.bookmarks.collectAsState(initial = emptyList()).value
                val editableBookmark =
                    mainViewModel.selectedBookmark.collectAsState(initial = null).value
                val bookmarkToStart = remember { mutableStateOf<Bookmark?>(null) }

                val permissionLauncher = requestNotificationPermission(bookmarkToStart)
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(title = { Text(stringResource(id = R.string.app_name)) },
                        navigationIcon = {
                            BackNavigation(isSinglePane, editableBookmark)
                        },
                        actions = {
                            SaveAction(editableBookmark)
                        })
                }, floatingActionButton = {
                    AddBookmarkButton(isSinglePane, editableBookmark)
                }) { paddingValues ->
                    TickerApp(
                        paddingValues = paddingValues,
                        isSinglePane = isSinglePane,
                        selectedBookmark = editableBookmark,
                        bookmarks = bookmarks
                    ) {
                        bookmarkToStart.value = it
                        if (needsPostNotificationPermission()) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            bookmarkToStart.startBookmark()
                        }
                    }
                }
            }
        }
    }


    @Composable
    private fun SaveAction(selectedBookmark: Bookmark?) {
        if (selectedBookmark != null) {
            IconButton(onClick = {
                mainViewModel.save(selectedBookmark)
                mainViewModel.select(null)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Save, contentDescription = "Save"
                )
            }
        }
    }

    @Composable
    private fun AddBookmarkButton(isSinglePane: Boolean, selectedBookmark: Bookmark?) {
        if (!isSinglePane || selectedBookmark == null) {
            FloatingActionButton(
                onClick = { mainViewModel.createNewBookmark() }, shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_bookmark)
                )
            }
        }
    }

    @Composable
    private fun BackNavigation(isSinglePane: Boolean, selectedBookmark: Bookmark?) {
        if (isSinglePane && selectedBookmark != null) {
            IconButton(onClick = { mainViewModel.select(null) }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack, contentDescription = "Navigation icon"
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    private fun isCompactOrInPortrait(
        windowSize: WindowSizeClass = calculateWindowSizeClass(activity = this),
        orientation: Int = LocalConfiguration.current.orientation
    ) =
        (windowSize.widthSizeClass == WindowWidthSizeClass.Compact || orientation == Configuration.ORIENTATION_PORTRAIT)

    @Composable
    private fun requestNotificationPermission(selectedBookmark: State<Bookmark?>): ManagedActivityResultLauncher<String, Boolean> {
        val askScheduleAlamPermission = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            selectedBookmark.startBookmark()
        }
        return rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted && needsPostNotificationPermission()) {
                requestScheduleAlarmPermission(askScheduleAlamPermission)
            } else if (isGranted) {
                selectedBookmark.startBookmark()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestScheduleAlarmPermission(scheduleAlarmLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(R.string.please_allow_alarm_scheduling)
            setPositiveButton(android.R.string.ok) { _, _ ->
                scheduleAlarmLauncher.launch(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
            setPositiveButton(android.R.string.cancel, null)
        }
    }

    private fun State<Bookmark?>.startBookmark() {
        value?.let { bookmark -> mainViewModel.startBookmark(this@MainActivity, bookmark) }
    }
}