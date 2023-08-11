package com.fanstaticapps.randomticker.ui.main.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.BookmarkAdd
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
) {
    if (bookmarks.isNotEmpty()) {
        LazyColumn(
            modifier = modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bookmarks) {
                BookmarkView(it, edit, start, stop)
            }
        }
    } else {
        EmptyView(
            Modifier.fillMaxSize(),
            imageVector = Icons.Outlined.BookmarkAdd,
            text = stringResource(id = R.string.add_bookmark)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookmarkView(
    bookmark: Bookmark,
    edit: (Bookmark) -> Unit,
    start: (Bookmark) -> Unit,
    stop: (Bookmark) -> Unit,
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
            ButtonRow(bookmark, stop, start)
        }
    }
}

@Composable
private fun ButtonRow(
    bookmark: Bookmark,
    stop: (Bookmark) -> Unit,
    start: (Bookmark) -> Unit
) {
    Row(horizontalArrangement = Arrangement.End) {
        if (bookmark.intervalEnd > System.currentTimeMillis()) {
            IconButton(onClick = { stop(bookmark) }) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = stringResource(id = android.R.string.cancel)
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

@Preview(showBackground = true)
@Composable
fun BookmarkListEmptyPreview() {
    AppTheme {
        BookmarkListOverview(
            bookmarks = listOf(
            ),
            edit = {},
            start = {}
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 200)
@Preview(showBackground = true)
@Composable
fun BookmarkListPreview() {
    AppTheme {
        BookmarkListOverview(
            bookmarks = listOf(
                Bookmark(maximumSeconds = 12, maximumHours = 1),
                Bookmark(maximumSeconds = 12, maximumHours = 1)
            ),
            edit = {},
            start = {}
        ) {}
    }
}
