package com.fanstaticapps.randomticker.ui.main.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.ui.main.MainTickerViewModel
import com.fanstaticapps.randomticker.ui.main.TimerItemUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomTimerAppContent(
    windowWidthSizeClass: WindowWidthSizeClass,
    viewModel: MainTickerViewModel,
    selectedForEdit: Long? = null
) {
    val timers by viewModel.timers.collectAsState(emptyList())

    var selectedTimerForEdit: Long? by remember { mutableStateOf(selectedForEdit) }
    var showEditorScreen by remember { mutableStateOf(false) }

    val onSaveTimer: (TimerItemUiState) -> Unit = { timerDetails ->
        viewModel.save(timerDetails)
        selectedTimerForEdit = null
        showEditorScreen = false
    }

    val onCancelEdit: () -> Unit = {
        selectedTimerForEdit = null
        showEditorScreen = false
    }

    val onDelete: () -> Unit = {
        selectedTimerForEdit?.let { viewModel.delete(it) }
        selectedTimerForEdit = null
        showEditorScreen = false
    }

    val isTwoPane = windowWidthSizeClass == WindowWidthSizeClass.Expanded

    Scaffold(
        floatingActionButton = {
            if (!isTwoPane && !showEditorScreen) {
                FloatingActionButton(onClick = {
                    selectedTimerForEdit = null
                    showEditorScreen = true
                }) {
                    Icon(Icons.Default.Add, stringResource(R.string.add_timer))
                }
            }
        }
    ) { paddingValues ->
        if (isTwoPane) {
            // Left pane: List
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    TimerListScreen(
                        timers = timers,
                        onTimerClick = { timer -> selectedTimerForEdit = timer },
                        onAddTimerClick = { /* FAB handles this in two-pane, or add a button */ },
                        onStartTimerClick = { }
                    )
                }

                // Right pane: Timer Editor
                VerticalDivider()

                Surface(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    NewEditTimerScreen(
                        timerDetails = timers.first { it.id == selectedTimerForEdit },
                        onSave = onSaveTimer,
                        onCancel = onCancelEdit,
                        onDelete = onDelete

                    )
                }
            }
        } else {
            // Single-pane layout for Compact/Medium width
            if (showEditorScreen) {
                NewEditTimerScreen(
                    timerDetails = timers.first { it.id == selectedTimerForEdit },
                    onSave = onSaveTimer,
                    onCancel = onCancelEdit,
                    onDelete = onDelete
                )
            } else {
                TimerListScreen(
                    timers = timers,
                    onTimerClick = { timer ->
                        selectedTimerForEdit = timer
                        showEditorScreen = true
                    },
                    onAddTimerClick = { /* Handled by FAB */ },
                    onStartTimerClick = { }
                )
            }
        }
    }
}

// --- Preview Provider for WindowWidthSizeClass ---
class WindowWidthSizeClassProvider : PreviewParameterProvider<WindowWidthSizeClass> {
    override val values: Sequence<WindowWidthSizeClass> = sequenceOf(
        WindowWidthSizeClass.Compact,
        WindowWidthSizeClass.Medium,
        WindowWidthSizeClass.Expanded
    )
}

private val viewModelBase = object : MainTickerViewModel {
    override val timers: Flow<List<TimerItemUiState>> = flowOf(
        listOf(
            TimerItemUiState(
                id = 1,
                name = "Morning Routine",
                minInterval = 10.minutes,
                maxInterval = 30.minutes,
                autoRepeat = true,
                timerEnd = 0,
                alarmSound = ""
            ),
            TimerItemUiState(
                id = 2,
                name = "Workout Routine",
                minInterval = 10.minutes,
                maxInterval = 30.minutes,
                autoRepeat = false,
                timerEnd = 0,
                alarmSound = ""
            )
        )
    )

    override fun start(id: Long) {}
    override fun cancelTicker(id: Long) {}
    override fun save(timerDetails: TimerItemUiState) {}
    override fun delete(id: Long) {}
}


// --- Previews for RandomTimerAppContent ---

@Preview(
    showBackground = true,
    widthDp = 360,
    name = "Compact Width Preview (Phone Portrait)"
)
@Composable
fun RandomTimerAppContentCompactPreview(
    @PreviewParameter(WindowWidthSizeClassProvider::class) windowWidthSizeClass: WindowWidthSizeClass
) {
    if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                RandomTimerAppContent(
                    windowWidthSizeClass = WindowWidthSizeClass.Compact,
                    viewModelBase
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 600,
    heightDp = 800,
    name = "Medium Width Preview (Tablet Portrait)"
)
@Composable
fun RandomTimerAppContentMediumPreview(
    @PreviewParameter(WindowWidthSizeClassProvider::class) windowWidthSizeClass: WindowWidthSizeClass
) {
    if (windowWidthSizeClass == WindowWidthSizeClass.Medium) {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                RandomTimerAppContent(
                    windowWidthSizeClass = WindowWidthSizeClass.Medium,
                    viewModelBase
                )
            }
        }
    }
}


@Preview(
    showBackground = true,
    widthDp = 1000,
    heightDp = 700,
    name = "Expanded Width Preview (Tablet Landscape)"
)
@Composable
fun RandomTimerAppContentExpandedPreview(
    @PreviewParameter(WindowWidthSizeClassProvider::class) windowWidthSizeClass: WindowWidthSizeClass
) {
    if (windowWidthSizeClass == WindowWidthSizeClass.Expanded) {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                RandomTimerAppContent(
                    windowWidthSizeClass = WindowWidthSizeClass.Expanded,
                    viewModelBase,
                    2
                )
            }
        }
    }
}