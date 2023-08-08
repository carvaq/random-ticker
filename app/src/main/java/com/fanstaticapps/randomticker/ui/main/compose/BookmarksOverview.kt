package com.fanstaticapps.randomticker.ui.main.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.Boundary

@Composable
fun BookmarkListOverview(
    modifier: Modifier = Modifier,
    bookmarks: List<Bookmark>,
    edit: (Bookmark) -> Unit,
    start: (Bookmark) -> Unit,
    stop: (Bookmark) -> Unit,
    delete: (Bookmark) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
    ) {
        items(bookmarks) {
            BookmarkView(it, edit, start, stop, delete)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookmarkView(
    bookmark: Bookmark,
    edit: (Bookmark) -> Unit,
    start: (Bookmark) -> Unit,
    stop: (Bookmark) -> Unit,
    delete: (Bookmark) -> Unit
) {
    Card(modifier = Modifier.clickable(onClick = { edit(bookmark) })) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = bookmark.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${bookmark.min.format()} - ${bookmark.max.format()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            ButtonRow(delete, bookmark, stop, start)
        }
    }
}

@Composable
private fun ButtonRow(
    delete: (Bookmark) -> Unit,
    bookmark: Bookmark,
    stop: (Bookmark) -> Unit,
    start: (Bookmark) -> Unit
) {
    Row(horizontalArrangement = Arrangement.End) {
        IconButton(onClick = { delete(bookmark) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.button_delete)
            )
        }
        if (bookmark.intervalEnd > System.currentTimeMillis()) {
            IconButton(onClick = { stop(bookmark) }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = stringResource(id = R.string.stop_button)
                )
            }
        } else {
            IconButton(onClick = { start(bookmark) }) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = stringResource(id = R.string.start_button)
                )
            }
        }
    }
}

@Composable
private fun Boundary.format(): String {
    return "$hours${stringResource(id = R.string.hours)} $minutes${stringResource(id = R.string.minutes)} $seconds${
        stringResource(
            id = R.string.seconds
        )
    }"
}

@Preview(showBackground = true, widthDp = 200)
@Preview(showBackground = true)
@Composable
fun BookmarkListPreview() {
    AppTheme {
        BookmarkListOverview(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            delete = {},
            edit = {},
            start = {},
            stop = {}
        )
    }
}
