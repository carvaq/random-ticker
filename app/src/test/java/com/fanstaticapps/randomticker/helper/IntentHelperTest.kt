package com.fanstaticapps.randomticker.helper

import com.fanstaticapps.randomticker.data.Bookmark
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class IntentHelperTest {
    private val context = RuntimeEnvironment.getApplication()
    private val bookmark = Bookmark(id = 101L, name = "Test Bookmark")

    @Test
    fun `getOpenAppPendingIntent returns non-null PendingIntent`() {
        val pendingIntent = IntentHelper.getOpenAppPendingIntent(context, bookmark)
        assertNotNull(pendingIntent)
    }

    @Test
    fun `getCancelActionPendingIntent returns non-null PendingIntent`() {
        val pendingIntent = IntentHelper.getCancelActionPendingIntent(context, bookmark)
        assertNotNull(pendingIntent)
    }

    @Test
    fun `getRepeatReceiverPendingIntent returns non-null PendingIntent`() {
        val pendingIntent = IntentHelper.getRepeatReceiverPendingIntent(context, bookmark)
        assertNotNull(pendingIntent)
    }

    @Test
    fun `getKlaxonActivityPendingIntent returns non-null PendingIntent`() {
        val pendingIntent = IntentHelper.getKlaxonActivityPendingIntent(context, bookmark)
        assertNotNull(pendingIntent)
    }
}