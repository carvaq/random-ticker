package com.fanstaticapps.randomticker.ui.main.compose

import android.content.res.Configuration
import android.widget.NumberPicker
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.Boundary
import com.fanstaticapps.randomticker.databinding.BoundaryBinding

@Composable
fun BookmarkCreateView(
    modifier: Modifier = Modifier,
    bookmarkState: MutableState<Bookmark>,
    delete: (Bookmark) -> Unit,
    isSinglePane: Boolean
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val bookmark = bookmarkState.value
        BookmarkName(bookmark.name) {
            bookmarkState.value = bookmark.copy(name = it)
        }
        Spacer(Modifier.height(16.dp))
        if (isSinglePane && LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                BoundaryView(
                    modifier = Modifier.weight(0.5f),
                    boundary = bookmark.min,
                    boundaryType = R.string.from
                ) { hours, minutes, seconds ->
                    bookmarkState.value = bookmark.copy(
                        minimumHours = hours,
                        minimumMinutes = minutes,
                        minimumSeconds = seconds
                    )
                }
                Spacer(Modifier.width(16.dp))
                BoundaryView(
                    modifier = Modifier.weight(0.5f),
                    boundary = bookmark.max,
                    boundaryType = R.string.to
                ) { hours, minutes, seconds ->
                    bookmarkState.value = bookmark.copy(
                        maximumHours = hours,
                        maximumMinutes = minutes,
                        maximumSeconds = seconds
                    )
                }
            }
        } else {
            Column {
                BoundaryView(
                    boundary = bookmark.min,
                    boundaryType = R.string.from
                ) { hours, minutes, seconds ->
                    bookmarkState.value = bookmark.copy(
                        minimumHours = hours,
                        minimumMinutes = minutes,
                        minimumSeconds = seconds
                    )
                }
                Spacer(Modifier.height(16.dp))
                BoundaryView(
                    boundary = bookmark.max,
                    boundaryType = R.string.to
                ) { hours, minutes, seconds ->
                    bookmarkState.value = bookmark.copy(
                        maximumHours = hours,
                        maximumMinutes = minutes,
                        maximumSeconds = seconds
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        AutoRepeat(bookmark.autoRepeat) { bookmarkState.value = bookmark.copy(autoRepeat = it) }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.padding(bottom = 24.dp),
            onClick = { delete(bookmarkState.value) },
            colors = ButtonDefaults.textButtonColors()
        ) {
            Text(
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = R.string.button_delete)
            )
        }
    }
}

@Composable
fun AutoRepeat(value: Boolean, onCheckedChanged: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = value, onCheckedChange = onCheckedChanged)
        Text(text = stringResource(id = R.string.auto_repeat))
    }
}

@Composable
fun BookmarkName(bookmarkName: String, updateName: (String) -> Unit) {
    val maxChar = 20
    val rememberFocused = remember { mutableStateOf(false) }
    OutlinedTextField(
        singleLine = true,
        value = bookmarkName,
        onValueChange = { if (it.length <= maxChar) updateName(it) },
        label = {
            Text(
                text = stringResource(id = R.string.bookmark_hint),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        supportingText = {
            if (rememberFocused.value) {
                Text(
                    text = "${bookmarkName.length} / $maxChar",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { rememberFocused.value = it.isFocused },
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}


@Composable
private fun BoundaryView(
    modifier: Modifier = Modifier,
    boundary: Boundary,
    @StringRes boundaryType: Int,
    changeListener: BoundaryChangeListener
) {
    Card(modifier) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(text = stringResource(id = boundaryType), fontWeight = FontWeight.Bold)
            AndroidViewBinding(
                modifier = Modifier
                    .heightIn(max = 240.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally), factory = BoundaryBinding::inflate
            ) {
                hours.initialize(
                    boundary,
                    { hours },
                    { changeListener.onChange(it, minutes, seconds) },
                    23,
                    boundaryType,
                    R.string.hours_label
                )
                minutes.initialize(
                    boundary,
                    { minutes },
                    { changeListener.onChange(hours, it, seconds) },
                    59,
                    boundaryType,
                    R.string.minutes_label
                )
                seconds.initialize(
                    boundary,
                    { seconds },
                    { changeListener.onChange(hours, minutes, it) },
                    59,
                    boundaryType,
                    R.string.seconds_label
                )
            }
        }
    }
}

private fun NumberPicker.initialize(
    boundary: Boundary,
    getValue: Boundary.() -> Int,
    setValue: Boundary.(Int) -> Unit,
    max: Int,
    @StringRes fromToResId: Int,
    @StringRes type: Int
) {
    value = boundary.getValue()
    minValue = 0
    maxValue = max
    contentDescription = "${context.getString(fromToResId)} ${context.getString(type)}"
    setOnValueChangedListener { _, _, value ->
        boundary.setValue(value)
    }
}

fun interface BoundaryChangeListener {
    fun onChange(hours: Int, minutes: Int, seconds: Int)
}


@Preview(device = Devices.TABLET, showBackground = true, heightDp = 1000)
@Composable
fun BookmarkCreateLandscapePreview() {
    BookmarkCreateView(bookmarkState = remember {
        mutableStateOf(Bookmark(maximumSeconds = 12, maximumHours = 1))
    }, delete = {}, isSinglePane = true)
}


@Preview(heightDp = 1000, showBackground = true)
@Composable
fun BookmarkCreatePreview() {
    BookmarkCreateView(bookmarkState = remember {
        mutableStateOf(Bookmark(maximumSeconds = 12, maximumHours = 1))
    }, delete = {}, isSinglePane = false)
}