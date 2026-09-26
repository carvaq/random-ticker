package com.fanstaticapps.randomticker.extensions

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IntentExtensionsTest {
    @Test
    fun `getBookmarkId returns ID when valid extra is present`() {
        val intent = Intent().apply { putExtra(EXTRA_BOOKMARK_ID, 42L) }
        assertEquals(42L, intent.getBookmarkId())
    }

    @Test
    fun `getBookmarkId returns null when extra is DEFAULT_BOOKMARK_ID or missing`() {
        val emptyIntent = Intent()
        assertNull(emptyIntent.getBookmarkId())

        val defaultIntent = Intent().apply { putExtra(EXTRA_BOOKMARK_ID, DEFAULT_BOOKMARK_ID) }
        assertNull(defaultIntent.getBookmarkId())
    }

    @Test
    fun `requireBookmarkId returns ID when present`() {
        val intent = Intent().apply { putExtra(EXTRA_BOOKMARK_ID, 99L) }
        assertEquals(99L, intent.requireBookmarkId())
    }

    @Test(expected = NullPointerException::class)
    fun `requireBookmarkId throws Exception when ID is missing`() {
        val intent = Intent()
        intent.requireBookmarkId()
    }

    @Test
    fun `resetBookmarkId sets extra to null`() {
        val intent = Intent().apply { putExtra(EXTRA_BOOKMARK_ID, 123L) }
        intent.resetBookmarkId()
        assertNull(intent.getBookmarkId())
    }
}