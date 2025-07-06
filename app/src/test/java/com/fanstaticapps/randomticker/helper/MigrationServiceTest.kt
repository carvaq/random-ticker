package com.fanstaticapps.randomticker.helper

import android.app.NotificationChannel
import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.getNotificationManager
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MigrationServiceTest {

    private lateinit var mockContext: Context
    private lateinit var mockBookmarkService: BookmarkService
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockSharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var migrationService: MigrationService
    private val mockNotificationManager = mockk<NotificationManagerCompat>("Noti", relaxed = true)

    private val testBookmark = Bookmark(id = 1, name = "Test Bookmark", soundUri = null)
    private val defaultAlarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockBookmarkService = mockk(relaxed = true)
        mockSharedPreferences = mockk(relaxed = true)
        mockSharedPreferencesEditor = mockk(relaxed = true)

        every {
            mockContext.getSharedPreferences(
                any(),
                Context.MODE_PRIVATE
            )
        } returns mockSharedPreferences
        every { mockSharedPreferences.edit() } returns mockSharedPreferencesEditor
        every {
            mockSharedPreferencesEditor.putInt(
                any(),
                any()
            )
        } returns mockSharedPreferencesEditor
        every { mockSharedPreferencesEditor.apply() } just Runs

        mockkStatic("com.fanstaticapps.randomticker.extensions.ContextExtensionsKt")
        every { mockContext.getNotificationManager() } returns mockNotificationManager

        migrationService = MigrationService(mockContext, mockBookmarkService)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `runMigrationIfNeeded should not run migration if already run`() {
        every {
            mockSharedPreferences.getInt(
                MigrationService.LAST_MIGRATION_VERSION_KEY,
                0
            )
        } returns 4

        migrationService.runMigrationIfNeeded()

        verify(exactly = 0) { mockBookmarkService.updateBookmarks(any()) }
        verify(exactly = 0) { mockSharedPreferencesEditor.putInt(any(), any()) }
    }

    @Test
    fun `migrateSoundSettings should update bookmark with default sound if soundUri is null and no old channel`() {
        val bookmarkSlot = slot<(Bookmark) -> Bookmark>()
        every { mockBookmarkService.updateBookmarks(capture(bookmarkSlot)) } returns mockk()
        needsToRunMigration()
        every { mockNotificationManager.getNotificationChannel(any()) } returns null

        migrationService.runMigrationIfNeeded()


        val originalBookmark = testBookmark.copy(soundUri = null)
        val updatedBookmark = bookmarkSlot.captured(originalBookmark)
        assertEquals(defaultAlarmSoundUri.toString(), updatedBookmark.soundUri)
        verify { mockBookmarkService.updateBookmarks(any()) }
    }

    @Test
    fun `migrateSoundSettings should update bookmark with sound from old channel if present`() {
        val customSoundUri = Uri.parse("content://media/custom_sound")
        val oldChannelId = "${testBookmark.id}-KLAXON"
        val mockChannel = mockk<NotificationChannel> {
            every { sound } returns customSoundUri
            every { id } returns oldChannelId
        }
        every { mockNotificationManager.getNotificationChannel(any()) } returns mockChannel

        val bookmarkSlot = slot<(Bookmark) -> Bookmark>()
        every { mockBookmarkService.updateBookmarks(capture(bookmarkSlot)) } returns mockk()

        needsToRunMigration()
        migrationService.runMigrationIfNeeded()

        val originalBookmark = testBookmark.copy(soundUri = null)
        val updatedBookmark = bookmarkSlot.captured(originalBookmark)
        assertEquals(customSoundUri.toString(), updatedBookmark.soundUri)
        verify { mockBookmarkService.updateBookmarks(any()) }
        verify { mockNotificationManager.deleteNotificationChannel(oldChannelId) }
    }

    @Test
    fun `migrateSoundSettings should not change bookmark if soundUri already exists`() {
        val existingSound = "content://media/existing_sound"
        val bookmarkWithSound = testBookmark.copy(soundUri = existingSound)

        val bookmarkSlot = slot<(Bookmark) -> Bookmark>()
        every { mockBookmarkService.updateBookmarks(capture(bookmarkSlot)) } returns mockk()

        needsToRunMigration()
        migrationService.runMigrationIfNeeded()


        val updatedBookmark = bookmarkSlot.captured(bookmarkWithSound)
        assertEquals(existingSound, updatedBookmark.soundUri)
        verify { mockBookmarkService.updateBookmarks(any()) }
    }

    private fun needsToRunMigration() {
        every {
            mockSharedPreferences.getInt(
                MigrationService.LAST_MIGRATION_VERSION_KEY,
                0
            )
        } returns 0
    }
}
