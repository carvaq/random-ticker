package com.fanstaticapps.randomticker.ui.main.compose

import android.widget.NumberPicker
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    bookmark: Bookmark,
    save: (Bookmark) -> Unit,
    delete: (Bookmark) -> Unit
) {
    Column(
        modifier = modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val minBoundary = remember { mutableStateOf(bookmark.min) }
        val maxBoundary = remember { mutableStateOf(bookmark.max) }
        val name = remember { mutableStateOf(bookmark.name) }
        val autoRepeat = remember { mutableStateOf(bookmark.autoRepeat) }
        BookmarkName(name.value) { name.value = it }
        Spacer(Modifier.height(16.dp))
        Column {
            BoundaryView(
                contentBindingModifier = Modifier
                    .heightIn(max = 240.dp)
                    .fillMaxWidth(),
                boundary = minBoundary,
                boundaryType = R.string.from
            )
            Spacer(Modifier.height(16.dp))
            BoundaryView(
                contentBindingModifier = Modifier
                    .heightIn(max = 240.dp)
                    .fillMaxWidth(),
                boundary = maxBoundary,
                boundaryType = R.string.to
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        AutoRepeat(autoRepeat.value) { autoRepeat.value = it }
        Spacer(modifier = Modifier.height(36.dp))
        SaveButton(minBoundary, maxBoundary, save, bookmark, name, autoRepeat)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { delete(bookmark) }, colors = ButtonDefaults.textButtonColors()) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = R.string.button_delete)
            )
        }

    }
}

@Composable
private fun SaveButton(
    minBoundary: MutableState<Boundary>,
    maxBoundary: MutableState<Boundary>,
    save: (Bookmark) -> Unit,
    bookmark: Bookmark,
    name: MutableState<String>,
    autoRepeat: MutableState<Boolean>
) {
    Button(modifier = Modifier.fillMaxWidth(),
        enabled = minBoundary.value < maxBoundary.value,
        onClick = {
            save(
                bookmark.updateBoundaries(minBoundary.value, maxBoundary.value)
                    .copy(name = name.value, autoRepeat = autoRepeat.value)
            )
        }) {
        Icon(imageVector = Icons.Default.Save, contentDescription = null)
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(id = R.string.save_button)
        )
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
    OutlinedTextField(
        value = bookmarkName,
        onValueChange = updateName,
        label = {
            Text(
                text = stringResource(id = R.string.bookmark_hint),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}


@Composable
private fun BoundaryView(
    modifier: Modifier = Modifier,
    contentBindingModifier: Modifier = Modifier,
    boundary: MutableState<Boundary>,
    @StringRes boundaryType: Int
) {
    Card(modifier) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(text = stringResource(id = boundaryType), fontWeight = FontWeight.Bold)
            AndroidViewBinding(
                modifier = contentBindingModifier
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally), factory = BoundaryBinding::inflate
            ) {
                hours.initialize(
                    boundary,
                    { hours },
                    { copy(hours = it) },
                    23,
                    boundaryType,
                    R.string.hours_label
                )
                minutes.initialize(
                    boundary,
                    { minutes },
                    { copy(minutes = it) },
                    59,
                    boundaryType,
                    R.string.minutes_label
                )
                seconds.initialize(
                    boundary,
                    { seconds },
                    { copy(seconds = it) },
                    59,
                    boundaryType,
                    R.string.seconds_label
                )
            }
        }
    }
}

private fun NumberPicker.initialize(
    boundary: MutableState<Boundary>,
    getValue: Boundary.() -> Int,
    setValue: Boundary.(Int) -> Boundary,
    max: Int,
    @StringRes fromToResId: Int,
    @StringRes type: Int
) {
    value = boundary.value.getValue()
    minValue = 0
    maxValue = max
    contentDescription = "${context.getString(fromToResId)} ${context.getString(type)}"
    setOnValueChangedListener { _, _, value ->
        boundary.value = boundary.value.setValue(value)

    }
}

@Preview(heightDp = 2000, showBackground = true)
@Composable
fun BookmarkCreatePreview() {
    BookmarkCreateView(bookmark = Bookmark(maximumSeconds = 12, maximumHours = 1),
        save = {},
        delete = {})
}