package com.fanstaticapps.randomticker.data

import androidx.compose.runtime.saveable.SaverScope
import org.junit.Assert.assertEquals
import org.junit.Test

class BookmarkSaverTest {
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