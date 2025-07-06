package com.fanstaticapps.randomticker.ui.main.compose

import android.content.Intent
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.EditNotifications
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.ui.main.MainViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.koin.androidx.compose.koinViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBar(
    isSinglePane: Boolean,
    editableBookmark: MutableState<Bookmark>?
) {
    TopAppBar(title = { Text(stringResource(id = R.string.app_name)) },
        navigationIcon = {
            BackNavigation(isSinglePane, editableBookmark)
        },
        actions = {
            NotificationSettingsAction(editableBookmark)
            SaveAction(editableBookmark)
            OverFlowAction()
        })
}

@Composable
private fun BackNavigation(
    isSinglePane: Boolean,
    selectedBookmark: State<Bookmark>?,
    mainViewModel: MainViewModel = koinViewModel()
) {
    if (isSinglePane && selectedBookmark != null) {
        IconButton(onClick = { mainViewModel.select(null) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigation icon"
            )
        }
    }
}

@Composable
private fun OverFlowAction() {
    var showMenu by remember { mutableStateOf(false) }
    IconButton(onClick = { showMenu = !showMenu }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null
        )
    }
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        val context = LocalContext.current
        DropdownMenuItem(onClick = {
            context.startActivity(
                Intent(context, OssLicensesMenuActivity::class.java)
            )
        }, text = {
            Text(text = stringResource(id = R.string.license))
        })
    }
}


@Composable
private fun SaveAction(
    selectedBookmark: State<Bookmark>?,
    mainViewModel: MainViewModel = koinViewModel()
) {
    if (selectedBookmark != null) {
        IconButton(
            onClick = {
                mainViewModel.save(selectedBookmark.value)
                mainViewModel.select(null)
            },
            enabled = selectedBookmark.value.min < selectedBookmark.value.max
        ) {
            Icon(
                imageVector = Icons.Outlined.Save, contentDescription = "Save"
            )
        }
    }
}

@Composable
private fun NotificationSettingsAction(selectedBookmark: State<Bookmark>?) {
    if (selectedBookmark != null) {
        val context = LocalContext.current
        IconButton(
            onClick = {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    putExtra(
                        Settings.EXTRA_CHANNEL_ID,
                        selectedBookmark.value.notificationChannelId
                    )
                }
                context.startActivity(intent)
            },
        ) {
            Icon(
                imageVector = Icons.Outlined.EditNotifications,
                contentDescription = stringResource(id = R.string.open_notification_channel_title)
            )
        }
    }
}

