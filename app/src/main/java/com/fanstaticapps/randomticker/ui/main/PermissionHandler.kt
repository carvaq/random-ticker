package com.fanstaticapps.randomticker.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.extensions.isAtLeastS
import com.fanstaticapps.randomticker.extensions.isAtLeastT
import com.fanstaticapps.randomticker.extensions.needsPostNotificationPermission
import com.fanstaticapps.randomticker.extensions.needsScheduleAlarmPermission

object PermissionHandler {
    private val requestNotificationPermissionInSettings = mutableStateOf(false)
    private val requestNotificationPermissionDialog = mutableStateOf(false)
    private val requestScheduleAlarmPermissionDialog = mutableStateOf(false)

    @Composable
    private fun PermissionRequestSnackbar() {
        val context = LocalContext.current
        Snackbar(
            action = {
                Button(onClick = {
                    requestNotificationPermissionInSettings.value = false
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", context.packageName, null)
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
    fun RequestPermissions(onPermissionsGranted: () -> Unit) {
        val context = LocalContext.current
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted && context.needsScheduleAlarmPermission()) {
                requestScheduleAlarmPermissionDialog.value = true
            } else if (isGranted) {
                onPermissionsGranted()
            } else {
                requestNotificationPermissionInSettings.value = true
            }
        }

        if (requestNotificationPermissionDialog.value && isAtLeastT()) {
            requestNotificationPermissionDialog.value = false
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else if (requestScheduleAlarmPermissionDialog.value && isAtLeastS()) {
            val askScheduleAlamPermission = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                requestNotificationPermissionDialog.value = false
                onPermissionsGranted()
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

    fun doActionWithPermissionsRequired(context: Context, onPermissionsGranted: () -> Unit) {
        if (context.needsPostNotificationPermission()) {
            requestNotificationPermissionDialog.value = true
        } else if (context.needsScheduleAlarmPermission()) {
            requestScheduleAlarmPermissionDialog.value = true
        } else {
            onPermissionsGranted()
        }
    }

    @Composable
    fun ShowSnackBarIfNecessary() {
        if (requestNotificationPermissionInSettings.value) {
            PermissionRequestSnackbar()
        }
    }
}