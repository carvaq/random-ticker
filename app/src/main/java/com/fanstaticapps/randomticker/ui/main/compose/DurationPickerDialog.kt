package com.fanstaticapps.randomticker.ui.main.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Dialog containing the DurationSelector for editing.
 */
@Composable
fun DurationPickerDialog(
    title: String,
    initialDuration: Duration,
    onConfirm: (Duration) -> Unit,
    onDismiss: () -> Unit
) = initialDuration.toComponents { h, m, s, _ ->
    var hours by remember { mutableStateOf(h.toString().padStart(2, '0')) }
    var minutes by remember { mutableStateOf(m.toString().padStart(2, '0')) }
    var seconds by remember { mutableStateOf(s.toString().padStart(2, '0')) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            DurationSelector(
                hours = hours,
                onHoursChange = { hours = it },
                minutes = minutes,
                onMinutesChange = { minutes = it },
                seconds = seconds,
                onSecondsChange = { seconds = it }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(hours.toIntOrZero().hours + minutes.toIntOrZero().minutes + seconds.toIntOrZero().seconds)
            }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun DurationSelector(
    hours: String,
    onHoursChange: (String) -> Unit,
    minutes: String,
    onMinutesChange: (String) -> Unit,
    seconds: String,
    onSecondsChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeInputField(value = hours, onValueChange = {
                if (it.length <= 2 && it.all { char -> char.isDigit() }) onHoursChange(it)
            }, label = "HH")

            Text(
                text = ":",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            TimeInputField(value = minutes, onValueChange = {
                if (it.length <= 2 && it.all { char -> char.isDigit() }) onMinutesChange(it)
            }, label = "MM")

            Text(
                text = ":",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            TimeInputField(value = seconds, onValueChange = {
                if (it.length <= 2 && it.all { char -> char.isDigit() }) onSecondsChange(it)
            }, label = "SS")
        }
    }
}


@Composable
private fun TimeInputField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.width(80.dp),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineSmall,
        shape = MaterialTheme.shapes.small
    )
}

private fun String.toIntOrZero() = toIntOrNull() ?: 0

@Preview
@Composable
private fun DialogPreview() {
    DurationPickerDialog("Title", 1.hours + 2.minutes + 3.seconds, {}, {})
}