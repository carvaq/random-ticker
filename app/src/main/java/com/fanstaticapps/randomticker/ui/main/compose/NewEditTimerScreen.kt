package com.fanstaticapps.randomticker.ui.main.compose

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.ui.main.TimerItemUiState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes


/**
 * Main screen for creating or editing a timer.
 * @param timerDetails The initial configuration to pre-fill the fields (for editing).
 * @param onSave Callback when the timer is saved.
 * @param onCancel Callback when the operation is cancelled.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEditTimerScreen(
    modifier: Modifier = Modifier,
    timerDetails: TimerItemUiState?,
    onSave: (TimerItemUiState) -> Unit,
    onCancel: () -> Unit,
) {
    var timerName by remember { mutableStateOf("") }
    var minInterval by remember { mutableStateOf(Duration.ZERO) }
    var maxInterval by remember { mutableStateOf(Duration.ZERO) }
    var autoRepeatEnabled by remember { mutableStateOf(false) }
    var alarmSoundUri by remember { mutableStateOf<String?>(null) }
    var alarmSoundName by remember { mutableStateOf("Default") }

    var showMinIntervalDialog by remember { mutableStateOf(false) }
    var showMaxIntervalDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(timerDetails) {
        timerName = timerDetails?.name ?: ""
        minInterval = timerDetails?.minInterval ?: Duration.ZERO
        maxInterval = timerDetails?.maxInterval ?: Duration.ZERO
        autoRepeatEnabled = timerDetails?.autoRepeat ?: false
        alarmSoundUri = timerDetails?.alarmSound
        alarmSoundName = if (timerDetails?.alarmSound != null) {
            timerDetails.alarmSound.toUri()
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }.getTitle(context)
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            readUri(activityResult)?.let { uri ->
                alarmSoundUri = uri.toString()
                alarmSoundName = uri.getTitle(context)
            }
        }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = timerName,
                onValueChange = { timerName = it },
                label = { Text(stringResource(R.string.app_name)) },
                placeholder = { Text("e.g., Pomodoro Break, Workout Set") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )


            Text(
                stringResource(R.string.set_random_interval),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            IntervalSelectorRow(
                label = stringResource(R.string.from),
                duration = minInterval,
                onClick = { showMinIntervalDialog = true }
            )
            Spacer(modifier = Modifier.height(8.dp))

            IntervalSelectorRow(
                label = stringResource(R.string.to),
                duration = maxInterval,
                onClick = { showMaxIntervalDialog = true }
            )


            HorizontalDivider(Modifier.padding(vertical = 24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.auto_repeat),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = autoRepeatEnabled,
                    onCheckedChange = { autoRepeatEnabled = it },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RingtoneSelector(launcher, alarmSoundName, alarmSoundUri)
        }

        ButtonRow(maxInterval > Duration.ZERO, onCancel) {
            val newConfig = TimerItemUiState(
                id = timerDetails?.id ?: 0,
                name = timerName,
                minInterval = minInterval,
                maxInterval = maxInterval,
                autoRepeat = autoRepeatEnabled,
                alarmSound = alarmSoundUri,
                timerEnd = 0
            )
            onSave(newConfig)
        }

        if (showMinIntervalDialog) {
            DurationPickerDialog(
                stringResource(R.string.minimum_interval_title),
                minInterval,
                {
                    minInterval = it
                    if (minInterval > maxInterval) {
                        maxInterval = minInterval + 999.milliseconds
                    }
                    showMinIntervalDialog = false
                },
                { showMinIntervalDialog = false })
        }
        if (showMaxIntervalDialog) {
            DurationPickerDialog(
                stringResource(R.string.maximum_interval_title),
                maxInterval,
                {
                    maxInterval = it
                    showMaxIntervalDialog = false
                },
                { showMaxIntervalDialog = false })
        }
    }

}

@Composable
private fun IntervalSelectorRow(label: String, duration: Duration, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(
                text = duration.toComponents { h, m, s, _ -> "${h}h ${m}m ${s}s" },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ButtonRow(canSave: Boolean, onCancel: () -> Unit, onSaveClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(stringResource(android.R.string.cancel))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = onSaveClick,
            enabled = canSave,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(R.string.save_button))
        }
    }
}

private fun readUri(result: ActivityResult): Uri? = result.data?.let { intent ->
    IntentCompat.getParcelableExtra(
        intent,
        RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
        Uri::class.java
    )
}

@Composable
private fun RingtoneSelector(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    alarmSoundName: String,
    alarmSoundUri: String?
) {
    val title = stringResource(R.string.select_alarm_sound)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                launcher.launch(Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                    putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL)
                    putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                    putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, title)
                    putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, alarmSoundUri?.toUri())
                })
            }
            .padding(horizontal = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.alarm_sound),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(R.string.selected_sound, alarmSoundName),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun Uri.getTitle(context: Context): String = RingtoneManager.getRingtone(context, this)
    .getTitle(context) ?: "Unknown Sound"

@Preview(showBackground = true)
@Composable
fun PreviewNewEditTimerScreen() {
    MaterialTheme {
        NewEditTimerScreen(
            timerDetails = null,
            onSave = { config -> println("Saved: $config") }
        ) { println("Cancelled") }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditExistingTimerScreen() {
    val existingConfig = TimerItemUiState(
        id = 1,
        name = "Morning Routine",
        minInterval = 10.minutes,
        maxInterval = 30.minutes,
        autoRepeat = true,
        timerEnd = 0,
        alarmSound = ""
    )
    MaterialTheme {
        NewEditTimerScreen(
            timerDetails = existingConfig,
            onSave = { config -> println("Updated: $config") }
        ) { println("Cancelled edit") }
    }
}