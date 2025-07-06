package com.fanstaticapps.randomticker.ui.main.compose

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.ui.main.TimerItemUiState
import com.fanstaticapps.randomticker.ui.main.TimersScreenUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TimerListScreen(
    timerState: TimersScreenUiState,
    onStartTimerAction: (Long) -> Unit,
    onStopTimerAction: (Long) -> Unit,
    onTimerClick: (Long) -> Unit,
    onAddTimerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    
    val context = LocalContext.current
    var showPermissionRationaleDialog by remember { mutableStateOf(false) }
    var timerIdPendingPermission by remember { mutableStateOf<Long?>(null) }
    var showPermanentlyDeniedDialog by remember { mutableStateOf(false) }
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }
    
    val attemptToStartTimer = { timerId: Long ->
        if (notificationPermissionState == null || notificationPermissionState.status.isGranted) {
            onStartTimerAction(timerId)
        } else {
            timerIdPendingPermission = timerId
            when {
                notificationPermissionState.status.shouldShowRationale -> {
                    showPermissionRationaleDialog = true
                }
                
                notificationPermissionState.status is PermissionStatus.Denied &&
                        (notificationPermissionState.status as PermissionStatus.Denied).shouldShowRationale -> {
                    showPermanentlyDeniedDialog = true
                }
                
                else -> {
                    notificationPermissionState.launchPermissionRequest()
                }
            }
        }
    }
    
    LaunchedEffect(notificationPermissionState?.status) {
        if (notificationPermissionState?.status?.isGranted == true && timerIdPendingPermission != null) {
            onStartTimerAction(timerIdPendingPermission!!)
            timerIdPendingPermission = null // Reset
        }
    }
    
    
    if (showPermissionRationaleDialog && timerIdPendingPermission != null) {
        RationaleDialog(
            title = stringResource(R.string.notification_permission_title),
            text = stringResource(R.string.notification_permission_rationale_timer),
            onConfirm = {
                showPermissionRationaleDialog = false
                notificationPermissionState?.launchPermissionRequest()
            },
            onDismiss = {
                showPermissionRationaleDialog = false
                timerIdPendingPermission = null // User dismissed rationale, cancel pending start
            }
        )
    }
    if (showPermanentlyDeniedDialog) {
        PermanentlyDeniedDialog(context) { showPermanentlyDeniedDialog = false }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        if (timerState is TimersScreenUiState.Success) {
            if (timerState.timers.isEmpty()) {
                NoTimersScreen(onAddTimerClick)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(timerState.timers) { timer ->
                        TimerCard(
                            timerState = timer,
                            onTogglePlayStopClick = {
                                if (timer.isRunning) {
                                    onStopTimerAction(timer.id)
                                } else {
                                    attemptToStartTimer(timer.id)
                                }
                            },
                            onEditClick = { onTimerClick(timer.id) }
                        )
                    }
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            
        }
    }
}

@Composable
private fun NoTimersScreen(onAddTimerClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_timers_message),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddTimerClick) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_timer)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.add_first_timer))
        }
    }
}

@Composable
private fun RationaleDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.button_grant_permission))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.button_cancel))
            }
        }
    )
}

@Composable
private fun PermanentlyDeniedDialog(context: Context, dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(stringResource(R.string.notification_permission_denied_title)) },
        text = { Text(stringResource(R.string.notification_permission_denied_message)) },
        confirmButton = {
            Button(onClick = {
                dismiss()
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                ).also { context.startActivity(it) }
            }) {
                Text(stringResource(R.string.button_open_settings))
            }
        },
        dismissButton = {
            Button(onClick = dismiss) {
                Text(stringResource(android.R.string.ok))
            }
        }
    )
}


@Composable
private fun TimerCard(
    timerState: TimerItemUiState,
    onTogglePlayStopClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (timerState.isRunning) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (timerState.isRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = timerState.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (timerState.isRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.interval_information,
                            timerState.minInterval,
                            timerState.maxInterval
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    AutoRepeatStatus(timerState)
                }
                
                IconButton(
                    onClick = { onTogglePlayStopClick() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (timerState.isRunning) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                        contentDescription = if (timerState.isRunning) {
                            stringResource(R.string.stop_timer)
                        } else {
                            stringResource(R.string.start_timer)
                        },
                        modifier = Modifier.size(32.dp), // Icon size
                        tint = if (timerState.isRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary // Make play icon primary color
                    )
                }
            }
            
            if (timerState.isRunning) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun AutoRepeatStatus(timerState: TimerItemUiState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (timerState.autoRepeat) Icons.Filled.Repeat else Icons.Outlined.Repeat, // Assuming you have an outlined version
            contentDescription = if (timerState.autoRepeat) "Auto-repeat ON" else "Auto-repeat OFF",
            modifier = Modifier.size(18.dp),
            tint = (if (timerState.isRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant).copy(
                alpha = 0.7f
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (timerState.autoRepeat) "Repeats" else "Does not repeat",
            style = MaterialTheme.typography.bodySmall,
            color = (if (timerState.isRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant).copy(
                alpha = 0.7f
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimerListScreenPreview() {
    val sampleTimers = listOf(
        TimerItemUiState(1, "Kitchen Timer", 5.minutes, 15.minutes, true, "", true),
        TimerItemUiState(2, "Workout", 30.seconds, 1.minutes, true, "", true),
        TimerItemUiState(3, "Study Session", 45.minutes, 1.hours, true, "", false)
    )
    MaterialTheme {
        TimerListScreen(
            timerState = TimersScreenUiState.Success(sampleTimers),
            onStartTimerAction = {},
            onStopTimerAction = {},
            onAddTimerClick = {},
            onTimerClick = {}
        )
    }
}