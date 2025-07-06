package com.fanstaticapps.randomticker.ui.main.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.ui.main.TimerItemUiState
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerListScreen(
    timers: List<TimerItemUiState>,
    onTogglePlayStopClick: (id: Long, stopTimer: Boolean) -> Unit,
    onTimerClick: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(timers) { timer ->
            TimerCard(
                timerState = timer,
                onTogglePlayStopClick = { onTogglePlayStopClick(timer.id, timer.isRunning) },
                onEditClick = { onTimerClick(timer.id) }
            )
        }
    }
}


@Composable
private fun TimerCard(
    timerState: TimerItemUiState,
    onTogglePlayStopClick: () -> Unit,
    onEditClick: () -> Unit
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
                verticalAlignment = Alignment.Top // Align to top to allow action button to be centered with text block
            ) {
                // Timer Info Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = timerState.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold, // Slightly less heavy than Bold
                        color = if (timerState.isRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Interval: ${timerState.minInterval} – ${timerState.maxInterval}",
                        style = MaterialTheme.typography.bodyMedium,
                        // Color is now handled by Card's contentColor
                    )
                    // Optional: Show more info like auto-repeat status
                    Spacer(modifier = Modifier.height(6.dp))
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

            // Progress Bar and Time Remaining (Conditional)
            if (timerState.isRunning) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary, // Progress bar color
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f) // Track color
                )
                // Optional: Display time remaining or next tick time if available
                // Text(text = "Next tick in: ${...}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp)) // Small space if no text below progress
            }
        }
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
            timers = sampleTimers,
            onTogglePlayStopClick = { id, stopTimer -> },
            onTimerClick = {}
        )
    }
}