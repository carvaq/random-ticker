@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.fanstaticapps.randomticker.ui.main.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.Boundary

@Composable
fun BookmarksOverview(
    paddingValues: PaddingValues,
    bookmarks: List<Bookmark>,
    edit: (Bookmark) -> Unit,
    start: (Bookmark) -> Unit,
    stop: (Bookmark) -> Unit,
    delete: (Bookmark) -> Unit,
    windowSize: WindowSizeClass
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        items(bookmarks) {
            BookmarkView(it, edit, start, stop, delete)
        }
    }
}

@Composable
private fun BookmarkView(
    bookmark: Bookmark,
    edit: (Bookmark) -> Unit,
    start: (Bookmark) -> Unit,
    stop: (Bookmark) -> Unit,
    delete: (Bookmark) -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = bookmark.name, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "${bookmark.min.format()} - ${bookmark.max.format()}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            IconButton(onClick = { delete(bookmark) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.button_delete)
                )
            }
            IconButton(onClick = { edit(bookmark) }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            }

            println("${bookmark.intervalEnd} ${System.currentTimeMillis()}")
            if (bookmark.intervalEnd > System.currentTimeMillis()) {
                IconButton(onClick = { stop(bookmark) }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.stop_button)
                    )
                }
            } else {
                IconButton(onClick = { start(bookmark) }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(id = R.string.start_button)
                    )
                }
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
fun TickerPreview() {
    AppTheme {
        BookmarksOverview(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
            delete = {},
            edit = {},
            start = {},
            paddingValues = PaddingValues(0.dp),
            stop = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 500)
@Composable
fun TickerPreviewTablet() {
    AppTheme {
        BookmarksOverview(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(700.dp, 500.dp)),
            delete = {},
            edit = {},
            start = {},
            paddingValues = PaddingValues(0.dp),
            stop = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 500, heightDp = 700)
@Composable
fun TickerPreviewTabletPortrait() {
    AppTheme {
        BookmarksOverview(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(500.dp, 700.dp)),
            delete = {},
            edit = {},
            start = {},
            paddingValues = PaddingValues(0.dp),
            stop = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1100, heightDp = 600)
@Composable
fun TickerPreviewDesktop() {
    AppTheme {
        BookmarksOverview(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(1100.dp, 600.dp)),
            delete = {},
            edit = {},
            start = {},
            paddingValues = PaddingValues(0.dp),
            stop = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 600, heightDp = 1100)
@Composable
fun TickerPreviewDesktopPortrait() {
    AppTheme {
        BookmarksOverview(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(600.dp, 1100.dp)),
            delete = {},
            edit = {},
            start = {},
            paddingValues = PaddingValues(0.dp),
            stop = {}
        )
    }
}