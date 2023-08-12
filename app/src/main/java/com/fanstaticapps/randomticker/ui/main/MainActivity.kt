package com.fanstaticapps.randomticker.ui.main

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkSaver
import com.fanstaticapps.randomticker.extensions.getBookmarkId
import com.fanstaticapps.randomticker.extensions.needsPostNotificationPermission
import com.fanstaticapps.randomticker.extensions.needsScheduleAlarmPermission
import com.fanstaticapps.randomticker.extensions.resetBookmarkId
import com.fanstaticapps.randomticker.helper.MigrationService
import com.fanstaticapps.randomticker.ui.BaseActivity
import com.fanstaticapps.randomticker.ui.main.PermissionHandler.RequestPermissions
import com.fanstaticapps.randomticker.ui.main.PermissionHandler.ShowSnackBarIfNecessary
import com.fanstaticapps.randomticker.ui.main.PermissionHandler.doActionWithPermissionsRequired
import com.fanstaticapps.randomticker.ui.main.compose.TickerApp
import com.fanstaticapps.randomticker.ui.main.compose.TopBar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private val migrationService: MigrationService by inject()

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
                //val bookmarkToStart = remember { mutableStateOf<Bookmark?>(null) }
                val permissionGrantedAction = remember { mutableStateOf({}) }
                RequestPermissions { permissionGrantedAction.value() }
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopBar(isSinglePane, editingBookmark)
                }, floatingActionButton = {
                    AddBookmarkButton(isSinglePane, editingBookmark, permissionGrantedAction)
                }, snackbarHost = {
                    ShowSnackBarIfNecessary()
                }) { paddingValues ->
                    TickerApp(
                        paddingValues = paddingValues,
                        isSinglePane = isSinglePane,
                        selectedBookmark = editingBookmark,
                        bookmarks = bookmarks,
                        start = {
                            permissionGrantedAction.value = { it.startBookmark() }
                            doActionWithPermissionsRequired(
                                this,
                                permissionGrantedAction.value
                            )
                        }
                    )
                }
            }
        }
    }


    @Composable
    private fun AddBookmarkButton(
        isSinglePane: Boolean,
        selectedBookmark: State<Bookmark>?,
        permissionGrantedAction: MutableState<() -> Unit>
    ) {
        permissionGrantedAction.value = { mainViewModel.createNewBookmark() }
        AnimatedVisibility(visible = !isSinglePane || selectedBookmark == null) {
            FloatingActionButton(
                onClick = {
                    doActionWithPermissionsRequired(
                        this@MainActivity,
                        permissionGrantedAction.value
                    )
                }, shape = CircleShape
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

    private fun Bookmark.startBookmark() {
        if (!needsPostNotificationPermission() && !needsScheduleAlarmPermission()) {
            mainViewModel.startBookmark(this)
        }
    }
}