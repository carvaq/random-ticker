@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.fanstaticapps.randomticker.ui.main.compose


import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService
import org.koin.compose.koinInject

@Composable
fun TickerApp(
    paddingValues: PaddingValues,
    windowSize: WindowSizeClass,
    bookmarks: List<Bookmark>,
    selectedBookmarkId: Long = NO_BOOKMARK_SELECTED,
    start: (Bookmark) -> Unit
) {
    val orientation = LocalConfiguration.current.orientation
    var selectedBookmark by rememberSaveable { mutableStateOf(bookmarks.find { it.id == selectedBookmarkId }) }
    val bookmarkService = koinInject<BookmarkService>()
    val context = LocalContext.current.applicationContext
    val delete = { bookmark: Bookmark -> bookmarkService.delete(context, bookmark) }
    val save = { bookmark: Bookmark ->
        bookmarkService.save(bookmark)
        selectedBookmark = null
    }
    val bookmark = selectedBookmark
    if (isCompactOrInPortrait(windowSize, orientation)) {
        if (bookmark == null) {
            BookmarkListOverview(
                modifier = Modifier.padding(paddingValues),
                bookmarks = bookmarks,
                edit = { selectedBookmark = it },
                start = start,
                stop = { bookmarkService.cancelTicker(context, it.id) },
                delete = delete
            )
        } else {
            BookmarkCreateView(
                modifier = Modifier.padding(paddingValues),
                bookmark = bookmark,
                save = save,
                delete = delete
            )
        }
    } else {
        Row(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            BookmarkListOverview(
                modifier = Modifier.weight(0.4f),
                bookmarks = bookmarks,
                edit = { selectedBookmark = it },
                start = start,
                stop = { bookmarkService.cancelTicker(context, it.id) },
                delete = delete
            )
            if (bookmark != null) {
                BookmarkCreateView(
                    modifier = Modifier.weight(0.6f),
                    bookmark = bookmark,
                    save = save,
                    delete = delete
                )
            } else {
                Box(modifier = Modifier.weight(0.6f))
            }
        }
    }
}

private const val NO_BOOKMARK_SELECTED = -1L

@Composable
private fun isCompactOrInPortrait(
    windowSize: WindowSizeClass,
    orientation: Int
) = (windowSize.widthSizeClass == WindowWidthSizeClass.Compact
        || orientation == Configuration.ORIENTATION_PORTRAIT)

@Preview(showBackground = true)
@Composable
fun TickerPreview() {
    AppTheme {
        TickerApp(
            paddingValues = PaddingValues(0.dp),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1))
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 500)
@Composable
fun TickerPreviewTablet() {
    AppTheme {
        TickerApp(
            paddingValues = PaddingValues(0.dp),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(700.dp, 500.dp)),
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1))
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 500, heightDp = 700)
@Composable
fun TickerPreviewTabletPortrait() {
    AppTheme {
        TickerApp(
            paddingValues = PaddingValues(0.dp),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(500.dp, 700.dp)),
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1))
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 1100, heightDp = 600)
@Composable
fun TickerPreviewDesktop() {
    AppTheme {
        TickerApp(
            selectedBookmarkId = 0,
            paddingValues = PaddingValues(0.dp),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(1100.dp, 600.dp)),
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1))
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 600, heightDp = 1100)
@Composable
fun TickerPreviewDesktopPortrait() {
    AppTheme {
        TickerApp(
            paddingValues = PaddingValues(0.dp),
            windowSize = WindowSizeClass.calculateFromSize(DpSize(600.dp, 1100.dp)),
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1))
        ) {}
    }
}