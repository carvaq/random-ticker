package com.fanstaticapps.randomticker.ui.main.compose

import android.widget.NumberPicker
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val minBoundary = remember { mutableStateOf(bookmark.min) }
        val maxBoundary = remember { mutableStateOf(bookmark.max) }
        Boundary(minBoundary, R.string.from)
        Spacer(Modifier.height(24.dp))
        Boundary(maxBoundary, R.string.to)
        Spacer(Modifier.height(36.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = { save(bookmark) }) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null)
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(id = R.string.save_button)
            )
        }
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
private fun Boundary(boundary: MutableState<Boundary>, @StringRes boundaryType: Int) {
    Card {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = stringResource(id = boundaryType), fontWeight = FontWeight.Bold)
            AndroidViewBinding(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                factory = BoundaryBinding::inflate
            ) {
                hours.init(boundary.value.hours, 23, boundaryType, R.string.hours_label)
                minutes.init(boundary.value.minutes, 59, boundaryType, R.string.minutes_label)
                seconds.init(boundary.value.seconds, 59, boundaryType, R.string.seconds_label)
                hours.setOnValueChangedListener { _, _, newValue ->
                    boundary.value = boundary.value.copy(hours = newValue)
                }
                minutes.setOnValueChangedListener { _, _, newValue ->
                    boundary.value = boundary.value.copy(minutes = newValue)
                }
                seconds.setOnValueChangedListener { _, _, newValue ->
                    boundary.value = boundary.value.copy(seconds = newValue)
                }
            }
        }
    }
}

private fun NumberPicker.init(
    currentValue: Int,
    max: Int,
    @StringRes fromToResId: Int,
    @StringRes type: Int
) {
    value = currentValue
    minValue = 0
    maxValue = max
    contentDescription = "${context.getString(fromToResId)} ${context.getString(type)}"
}

@Preview
@Composable
fun BookmarkCreatePreview() {
    BookmarkCreateView(
        bookmark = Bookmark(maximumSeconds = 12, maximumHours = 1),
        save = {},
        delete = {})
}