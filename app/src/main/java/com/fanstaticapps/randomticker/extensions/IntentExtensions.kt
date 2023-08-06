package com.fanstaticapps.randomticker.extensions

import android.content.Intent
import androidx.lifecycle.SavedStateHandle


const val EXTRA_BOOKMARK_ID = "EXTRA_BOOKMARK_ID"
const val DEFAULT_BOOKMARK_ID = -1L
fun Intent.getBookmarkId(): Long? =
    getLongExtra(EXTRA_BOOKMARK_ID, DEFAULT_BOOKMARK_ID).takeIf { it != DEFAULT_BOOKMARK_ID }

fun SavedStateHandle.getBookmarkId(): Long? = get(EXTRA_BOOKMARK_ID)