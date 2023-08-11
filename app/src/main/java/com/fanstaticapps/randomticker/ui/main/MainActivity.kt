package com.fanstaticapps.randomticker.ui.main

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkSaver
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import com.fanstaticapps.randomticker.extensions.isAtLeastS
import com.fanstaticapps.randomticker.extensions.isAtLeastT
import com.fanstaticapps.randomticker.extensions.needsPostNotificationPermission
import com.fanstaticapps.randomticker.extensions.needsScheduleAlarmPermission
import com.fanstaticapps.randomticker.extensions.resetBookmarkId
import com.fanstaticapps.randomticker.helper.MigrationService
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.main.compose.TickerApp
import com.fanstaticapps.randomticker.ui.main.compose.TopBar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private val migrationService: MigrationService by inject()
    private val requestNotificationInSettings = mutableStateOf(false)
    private val requestNotificationPermission = mutableStateOf(false)
    private val requestScheduleAlarmPermission = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getBookmarkId()?.let {
            mainViewModel.select(it)
            intent.resetBookmarkId()
        }
        migrationService.migrate()
        setContent {
            AppTheme {
                val isSinglePane = isCompactOrInPortrait()
                val bookmarks = mainViewModel.bookmarks.collectAsState(initial = emptyList()).value
                val editingBookmark =
                    mainViewModel.selectedBookmark.collectAsState(initial = null).value?.let {
                        rememberSaveable(stateSaver = BookmarkSaver.saver) { mutableStateOf(it) }
                    }
                val bookmarkToStart = remember { mutableStateOf<Bookmark?>(null) }
                RequestPermissions(bookmarkToStart)
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopBar(isSinglePane, editingBookmark)
                }, floatingActionButton = {
                    AddBookmarkButton(isSinglePane, editingBookmark)
                }, snackbarHost = {
                    if (requestNotificationInSettings.value) {
                        PermissionRequestSnackbar()
                    }

                }) { paddingValues ->
                    TickerApp(
                        paddingValues = paddingValues,
                        isSinglePane = isSinglePane,
                        selectedBookmark = editingBookmark,
                        bookmarks = bookmarks,
                        start = startBookmark(bookmarkToStart)
                    )
                }
            }
        }
    }

    @Composable
    private fun PermissionRequestSnackbar() {
        Snackbar(
            action = {
                Button(onClick = {
                    requestNotificationInSettings.value = false
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", packageName, null)
                    })
                }) {
                    Text(text = stringResource(id = R.string.open_settings))
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = stringResource(id = R.string.notification_settings_blocked))
        }
    }

    @Composable
    private fun RequestPermissions(selectedBookmark: MutableState<Bookmark?>) {
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted && needsScheduleAlarmPermission()) {
                requestScheduleAlarmPermission.value = true
            } else if (isGranted) {
                selectedBookmark.startBookmark()
            } else {
                requestNotificationInSettings.value = true
            }
        }

        if (requestNotificationPermission.value && isAtLeastT()) {
            requestNotificationPermission.value = false
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else if (requestScheduleAlarmPermission.value && isAtLeastS()) {
            val askScheduleAlamPermission = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                requestNotificationPermission.value = false
                selectedBookmark.startBookmark()
            }
            AlertDialog(
                confirmButton = {
                    Button(onClick = { askScheduleAlamPermission.launch(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)) }) {
                        Text(stringResource(id = android.R.string.ok))
                    }
                },
                dismissButton = { Text(stringResource(id = android.R.string.cancel)) },
                text = { Text(stringResource(id = R.string.please_allow_alarm_scheduling)) },
                onDismissRequest = {})
        }
    }

    private fun startBookmark(bookmarkToStart: MutableState<Bookmark?>): (Bookmark) -> Unit = {
        bookmarkToStart.value = it
        if (needsPostNotificationPermission()) {
            requestNotificationPermission.value = true
        } else if (needsScheduleAlarmPermission()) {
            requestScheduleAlarmPermission.value = true
        } else {
            bookmarkToStart.startBookmark()
        }
    }

    @Composable
    private fun AddBookmarkButton(isSinglePane: Boolean, selectedBookmark: State<Bookmark>?) {
        AnimatedVisibility(visible = !isSinglePane || selectedBookmark == null) {
            FloatingActionButton(
                onClick = { mainViewModel.createNewBookmark() }, shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdd,
                    contentDescription = stringResource(id = R.string.add_bookmark)
                )
            }
        }
    }


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    private fun isCompactOrInPortrait(
        windowSize: WindowSizeClass = calculateWindowSizeClass(activity = this),
        orientation: Int = LocalConfiguration.current.orientation
    ): Boolean {
        return windowSize.widthSizeClass == WindowWidthSizeClass.Compact
                || windowSize.heightSizeClass == WindowHeightSizeClass.Compact
                || orientation == Configuration.ORIENTATION_PORTRAIT
    }

    private fun State<Bookmark?>.startBookmark() {
        if (!needsPostNotificationPermission() && !needsScheduleAlarmPermission()) {
            value?.let { bookmark -> mainViewModel.startBookmark(bookmark) }
        }
    }
}