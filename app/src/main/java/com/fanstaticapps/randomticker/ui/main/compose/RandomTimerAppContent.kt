package com.fanstaticapps.randomticker.ui.main.compose

import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.ui.main.MainTickerViewModel
import com.fanstaticapps.randomticker.ui.main.TimerItemUiState
import com.fanstaticapps.randomticker.ui.main.compose.SelectionStatus.Editing
import com.fanstaticapps.randomticker.ui.main.compose.SelectionStatus.New
import com.fanstaticapps.randomticker.ui.main.compose.SelectionStatus.NotSelected
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration.Companion.minutes

private sealed interface SelectionStatus {
    object NotSelected : SelectionStatus
    data class Editing(val timerId: Long) : SelectionStatus
    object New : SelectionStatus

    val id: Long? get() = (this as? Editing)?.timerId
}

private val SelectionStatusSaver = listSaver<SelectionStatus, Any>(
    save = { status ->
        when (status) {
            NotSelected -> listOf("NotSelected")
            is Editing -> listOf("Editing", status.timerId)
            New -> listOf("New")
        }
    },
    restore = { list ->
        when (list.getOrNull(0)) {
            "NotSelected" -> NotSelected
            "Editing" -> Editing(list[1] as Long)
            else -> New
        }
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomTimerAppContent(
    windowWidthSizeClass: WindowWidthSizeClass,
    viewModel: MainTickerViewModel
) {
    val timers by viewModel.timers.collectAsState(emptyList())

    var selectionStatus: SelectionStatus by rememberSaveable(stateSaver = SelectionStatusSaver) {
        mutableStateOf(NotSelected)
    }

    val hideEditor = {
        selectionStatus = NotSelected
    }

    val onSaveTimer: (TimerItemUiState) -> Unit = { timerDetails ->
        viewModel.save(timerDetails)
        hideEditor()
    }

    val onCancelEdit = hideEditor

    val onDelete: () -> Unit = {
        selectionStatus.id?.let { viewModel.delete(it) }
        hideEditor()
    }
    val onStartTimer: (Long) -> Unit = { timerId ->
        viewModel.start(timerId)
    }
    val onStopTimer: (Long) -> Unit = { timerId ->
        viewModel.cancelTimer(timerId)
    }


    val isTwoPane = windowWidthSizeClass == WindowWidthSizeClass.Expanded

    Scaffold(
        floatingActionButton = {
            if (selectionStatus is NotSelected) {
                FloatingActionButton(onClick = { selectionStatus = New }) {
                    Icon(Icons.Default.Add, stringResource(R.string.add_timer))
                }
            }
        },
        topBar = { TopBar(selectionStatus, onDelete, onCancelEdit) }

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
                        onStartTimer,
                        onStopTimer,
                        onTimerClick = { selectionStatus = Editing(it) },
                        onAddTimerClick = { selectionStatus = New }
                    )
                }

                // Right pane: Timer Editor
                Surface(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    if (selectionStatus is NotSelected) {
                        EmptyEditorScreen()
                    } else {
                        NewEditTimerScreen(
                            timerDetails = timers.firstOrNull { it.id == selectionStatus.id },
                            onSave = onSaveTimer,
                            onCancel = onCancelEdit
                        )
                    }
                }
            }
        } else {
            // Single-pane layout for Compact/Medium width
            if (selectionStatus is NotSelected) {
                TimerListScreen(
                    timers = timers,
                    onStartTimerAction = onStartTimer,
                    onStopTimerAction = onStopTimer,
                    onTimerClick = { selectionStatus = Editing(it) },
                    modifier = Modifier.padding(paddingValues),
                    onAddTimerClick = { selectionStatus = New }
                )
            } else {
                NewEditTimerScreen(
                    modifier = Modifier.padding(paddingValues),
                    timerDetails = timers.firstOrNull { it.id == selectionStatus.id },
                    onSave = onSaveTimer,
                    onCancel = onCancelEdit
                )

            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    selectionStatus: SelectionStatus,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = when (selectionStatus) {
                    NotSelected -> R.string.app_name
                    is Editing -> R.string.edit_timer
                    New -> R.string.create_timer
                }.let { stringResource(it) }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            if (selectionStatus is Editing) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.button_delete)
                    )
                }
            }
            OverFlowAction()
        },
        navigationIcon = {
            if (selectionStatus !is NotSelected) {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.button_delete)
                    )
                }
            }
        }
    )
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

// --- Preview Provider for WindowWidthSizeClass ---
class WindowWidthSizeClassProvider : PreviewParameterProvider<WindowWidthSizeClass> {
    override val values: Sequence<WindowWidthSizeClass> = sequenceOf(
        WindowWidthSizeClass.Compact,
        WindowWidthSizeClass.Medium,
        WindowWidthSizeClass.Expanded
    )
}

private val viewModelStub = object : MainTickerViewModel {
    override val timers: Flow<List<TimerItemUiState>> = flowOf(
        listOf(
            TimerItemUiState(
                id = 1,
                name = "Morning Routine",
                minInterval = 10.minutes,
                maxInterval = 30.minutes,
                autoRepeat = true,
                alarmSound = "",
                isRunning = false
            ),
            TimerItemUiState(
                id = 2,
                name = "Workout Routine",
                minInterval = 10.minutes,
                maxInterval = 30.minutes,
                autoRepeat = false,
                alarmSound = "",
                isRunning = false
            )
        )
    )

    override fun start(id: Long) {}
    override fun cancelTimer(id: Long) {}
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
                    viewModelStub
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
                    viewModelStub
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
                    viewModelStub
                )
            }
        }
    }
}