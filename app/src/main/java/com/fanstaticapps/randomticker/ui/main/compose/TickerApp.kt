@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.fanstaticapps.randomticker.ui.main.compose


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark

@Composable
fun TickerApp(
    bookmarks: List<Bookmark>,
    windowSize: WindowSizeClass,
    delete: (Bookmark) -> Unit,
    edit: (Bookmark) -> Unit,
    start: (Bookmark) -> Unit,
    paddingValues: PaddingValues,
    stop: (Bookmark) -> Unit
) {
    if (windowSize.widthSizeClass == WindowWidthSizeClass.Compact) {
        BookmarkListOverview(
            modifier = Modifier.padding(paddingValues),
            bookmarks = bookmarks,
            edit = edit,
            start = start,
            stop = stop,
            delete = delete
        )
    } else {
        Row(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            BookmarkListOverview(
                modifier = Modifier.weight(0.4f),
                bookmarks = bookmarks,
                edit = edit,
                start = start,
                stop = stop,
                delete = delete
            )
            Box(modifier = Modifier.weight(0.6f))

        }
    }
}

@Preview(showBackground = true)
@Composable
fun TickerPreview() {
    AppTheme {
        TickerApp(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
            delete = {},
            edit = {},
            start = {},
            paddingValues = PaddingValues(0.dp)
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 500)
@Composable
fun TickerPreviewTablet() {
    AppTheme {
        TickerApp(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(700.dp, 500.dp)),
            delete = {},
            edit = {},
            start = {},
            paddingValues = PaddingValues(0.dp)
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 500, heightDp = 700)
@Composable
fun TickerPreviewTabletPortrait() {
    AppTheme {
        TickerApp(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(500.dp, 700.dp)),
            delete = {},
            edit = {},
            start = {},
            paddingValues = PaddingValues(0.dp)
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 1100, heightDp = 600)
@Composable
fun TickerPreviewDesktop() {
    AppTheme {
        TickerApp(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(1100.dp, 600.dp)),
            delete = {},
            edit = {},
            start = {},
            paddingValues = PaddingValues(0.dp)
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 600, heightDp = 1100)
@Composable
fun TickerPreviewDesktopPortrait() {
    AppTheme {
        TickerApp(
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1)),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(600.dp, 1100.dp)),
            delete = {},
            edit = {},
            start = {},
            paddingValues = PaddingValues(0.dp)
        ) {}
    }
}