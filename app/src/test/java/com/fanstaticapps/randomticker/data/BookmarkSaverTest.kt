package com.fanstaticapps.randomticker.data

import androidx.compose.runtime.saveable.SaverScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BookmarkSaverTest {
    @Test
    fun `BookmarkSaver saves and restores all bookmark properties including soundUri`() {
        val original =
            Bookmark(
                id = 42L,
                name = "Test Ticker",
                minimumHours = 1,
                minimumMinutes = 2,
                minimumSeconds = 3,
                maximumHours = 4,
                maximumMinutes = 5,
                maximumSeconds = 6,
                autoRepeat = true,
                autoRepeatInterval = 10000L,
                intervalEnd = 123456789L,
                soundUri = "content://media/internal/audio/media/100"
            )

        val saverScope = SaverScope { true }

        val saved = BookmarkSaver.saver.run { saverScope.save(original) }
        val restored = saved?.let { BookmarkSaver.saver.restore(it) }

        assertEquals(original, restored)
        assertEquals("content://media/internal/audio/media/100", restored?.soundUri)
    }

    @Test
    fun `Bookmark request codes are deterministic and unique per bookmark id`() {
        val bookmark1 = Bookmark(id = 5L)
        val bookmark2 = Bookmark(id = 5L)

        assertEquals(bookmark1.openAppRequestCode, bookmark2.openAppRequestCode)
        assertEquals(bookmark1.cancelActionRequestCode, bookmark2.cancelActionRequestCode)
        assertEquals(bookmark1.repeatReceiverRequestCode, bookmark2.repeatReceiverRequestCode)
        assertEquals(bookmark1.klaxonActivityRequestCode, bookmark2.klaxonActivityRequestCode)

        assertNotEquals(bookmark1.openAppRequestCode, bookmark1.cancelActionRequestCode)
        assertNotEquals(bookmark1.cancelActionRequestCode, bookmark1.repeatReceiverRequestCode)
        assertNotEquals(bookmark1.repeatReceiverRequestCode, bookmark1.klaxonActivityRequestCode)
    }

    @Test
    fun `saver saves and restores bookmark correctly`() {
        val originalBookmark =
            Bookmark(
                id = 123L,
                name = "Saved Bookmark",
                minimumHours = 1,
                minimumMinutes = 10,
                minimumSeconds = 20,
                maximumHours = 2,
                maximumMinutes = 30,
                maximumSeconds = 40,
                autoRepeat = true,
                autoRepeatInterval = 3000L,
                intervalEnd = 50000L
            )

        val scope = SaverScope { true }
        val savedMap = with(BookmarkSaver.saver) { scope.save(originalBookmark) }
        val restoredBookmark = BookmarkSaver.saver.restore(savedMap!!)

        assertEquals(originalBookmark.id, restoredBookmark?.id)
        assertEquals(originalBookmark.name, restoredBookmark?.name)
        assertEquals(originalBookmark.minimumHours, restoredBookmark?.minimumHours)
        assertEquals(originalBookmark.minimumMinutes, restoredBookmark?.minimumMinutes)
        assertEquals(originalBookmark.minimumSeconds, restoredBookmark?.minimumSeconds)
        assertEquals(originalBookmark.maximumHours, restoredBookmark?.maximumHours)
        assertEquals(originalBookmark.maximumMinutes, restoredBookmark?.maximumMinutes)
        assertEquals(originalBookmark.maximumSeconds, restoredBookmark?.maximumSeconds)
        assertEquals(originalBookmark.autoRepeat, restoredBookmark?.autoRepeat)
        assertEquals(originalBookmark.autoRepeatInterval, restoredBookmark?.autoRepeatInterval)
        assertEquals(originalBookmark.intervalEnd, restoredBookmark?.intervalEnd)
    }
}