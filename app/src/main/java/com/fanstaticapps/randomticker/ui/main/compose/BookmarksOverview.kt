package com.fanstaticapps.randomticker.ui.main.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.compose.AppTheme
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.Boundary
import com.fanstaticapps.randomticker.ui.main.MainViewModel
import com.fanstaticapps.randomticker.ui.main.PermissionHandler.doActionWithPermissionsRequired
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookmarkListOverview(
    modifier: Modifier = Modifier,
    bookmarks: List<Bookmark>,
    edit: (Bookmark) -> Unit,
    start: (Bookmark) -> Unit,
    stop: (Bookmark) -> Unit,
    permissionGrantedAction: MutableState<() -> Unit>,
) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(bookmarks) {
            BookmarkView(it, edit, start, stop)
        }
        item {
            val context = LocalContext.current
            val viewModel = koinViewModel<MainViewModel>()
            Button(
                onClick = {
                    permissionGrantedAction.value = { viewModel.createNewBookmark() }
                    doActionWithPermissionsRequired(
                        context, permissionGrantedAction.value
                    )

                },
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Outlined.BookmarkAdd, contentDescription = null,
                )
                Text(
                    text = stringResource(id = R.string.add_bookmark),
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
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
            start = {},
            stop = {},
            permissionGrantedAction = remember { mutableStateOf({}) }
        )
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
            start = {},
            stop = {},
            permissionGrantedAction = remember { mutableStateOf({}) }
        )
    }
}
