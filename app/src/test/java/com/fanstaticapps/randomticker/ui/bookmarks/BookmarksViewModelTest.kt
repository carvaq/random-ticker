package com.fanstaticapps.randomticker.ui.bookmarks

import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.fanstaticapps.randomticker.CoroutineTestRule
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class BookmarksViewModelTest {

    @get:Rule
    val coroutinesRule = CoroutineTestRule()

    private val tickerPreferences =
        spyk(TickerPreferences(ApplicationProvider.getApplicationContext()))
    private val repositoryMock: BookmarkRepository = mockk(relaxed = true, relaxUnitFun = true)

    private val testee =
        BookmarksViewModel(tickerPreferences, repositoryMock, coroutinesRule.testDispatcher)

    @Test
    fun `when deleting a bookmark then delete in on the repository`() =
        coroutinesRule.runBlockingTest {
            //given
            val bookmarkToBeDeleted = Bookmark("Test Bookmark")

            //when
            testee.deleteBookmark(bookmarkToBeDeleted)

            //then
            coVerify { repositoryMock.deleteBookmark(eq(bookmarkToBeDeleted)) }
        }

    @Test
    fun `when selecting a bookmark save it to ticker preferences`() =
        coroutinesRule.runBlockingTest {
            //given
            val bookmarkId: Long = 29
            val selectedBookmark = Bookmark(id = bookmarkId, name = "Test Bookmark")

            val mockedObserver: Observer<Long> = mockk(relaxed = true, relaxUnitFun = true)
            tickerPreferences.currentSelectedBookmarkIdAsLiveData.observeForever(mockedObserver)

            //when
            testee.selectBookmark(selectedBookmark)

            //then
            verify { tickerPreferences.currentSelectedId = bookmarkId }
            coVerify { mockedObserver.onChanged(bookmarkId) }
        }

    @Test
    fun `when creating a new bookmark save it ticker preferences`() =
        coroutinesRule.runBlockingTest {
            //given
            val bookmarkId: Long = 20
            coEvery { repositoryMock.insertOrUpdateBookmark(any()) } returns bookmarkId

            val newBookmark = Bookmark("New")
            val mockedObserver: Observer<Long> = mockk(relaxed = true, relaxUnitFun = true)
            tickerPreferences.currentSelectedBookmarkIdAsLiveData.observeForever(mockedObserver)

            //when
            testee.createBookmark(newBookmark)

            //then
            verify { tickerPreferences.currentSelectedId = bookmarkId }
            verify { mockedObserver.onChanged(bookmarkId) }
            coVerify { repositoryMock.insertOrUpdateBookmark(eq(newBookmark)) }
        }

}