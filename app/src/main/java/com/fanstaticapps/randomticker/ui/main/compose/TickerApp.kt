package com.fanstaticapps.randomticker.ui.main.compose


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.ui.main.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TickerApp(
    viewModel: MainViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    isSinglePane: Boolean,
    bookmarks: List<Bookmark>,
    selectedBookmark: MutableState<Bookmark>? = null,
    start: (Bookmark) -> Unit
) {
    val context = LocalContext.current.applicationContext
    val delete = { bookmark: Bookmark -> viewModel.delete(context, bookmark) }
    if (isSinglePane) {
        if (selectedBookmark == null) {
            BookmarkListOverview(
                modifier = Modifier.padding(paddingValues),
                bookmarks = bookmarks,
                edit = { viewModel.select(it.id) },
                start = start
            ) { viewModel.cancelTicker(context, it.id) }
        } else {
            BookmarkCreateView(
                modifier = Modifier.padding(paddingValues),
                bookmarkState = selectedBookmark,
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
                edit = { viewModel.select(it.id) },
                start = start
            ) { viewModel.cancelTicker(context, it.id) }
            if (selectedBookmark != null) {
                BookmarkCreateView(
                    modifier = Modifier.weight(0.6f),
                    bookmarkState = selectedBookmark,
                    delete = delete
                )
            } else {
                Box(modifier = Modifier.weight(0.6f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TickerPreview() {
    AppTheme {
        TickerApp(
            paddingValues = PaddingValues(0.dp),
            isSinglePane = true,
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1))
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 500)
@Composable
fun TickerPreviewTable() {
    AppTheme {
        TickerApp(
            paddingValues = PaddingValues(0.dp),
            isSinglePane = false,
            bookmarks = listOf(Bookmark(maximumSeconds = 12, maximumHours = 1))
        ) {}
    }
}