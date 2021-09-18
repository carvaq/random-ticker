package com.fanstaticapps.randomticker.ui.bookmarks

import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.fanstaticapps.randomticker.CoroutineTestRule
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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
        spy(TickerPreferences(ApplicationProvider.getApplicationContext()))
    private val repositoryMock: BookmarkRepository = mock()

    private val testee =
        BookmarksViewModel(SavedStateHandle(emptyMap()), tickerPreferences, repositoryMock)

    @Test
    fun `when deleting a bookmark then delete in on the repository`() =
        coroutinesRule.runBlockingTest {
            //given
            val bookmarkToBeDeleted = Bookmark("Test Bookmark")

            //when
            testee.deleteBookmark(bookmarkToBeDeleted)

            //then
            verify(repositoryMock).deleteBookmark(eq(bookmarkToBeDeleted))
        }

    @Test
    fun `when selecting a bookmark save it to ticker preferences`() {
        //given
        val selectedBookmark = Bookmark(id = 29, name = "Test Bookmark")

        val mockedObserver: Observer<Long> = mock()
        tickerPreferences.currentSelectedBookmarkIdAsLiveData.observeForever(mockedObserver)

        //when
        testee.selectBookmark(selectedBookmark)

        //then
        verify(tickerPreferences).currentSelectedId = 29
        verify(mockedObserver).onChanged(eq(29))
    }

    @Test
    fun `when creating a new bookmark save it ticker preferences`() =
        coroutinesRule.runBlockingTest {
            //given
            whenever(repositoryMock.insertOrUpdateBookmark(any())).doReturn(20)

            val newBookmark = Bookmark("New")
            val mockedObserver: Observer<Long> = mock()
            tickerPreferences.currentSelectedBookmarkIdAsLiveData.observeForever(mockedObserver)

            //when
            testee.createBookmark(newBookmark)

            //then
            verify(tickerPreferences).currentSelectedId = 20
            verify(mockedObserver).onChanged(eq(20))
            verify(repositoryMock).insertOrUpdateBookmark(eq(newBookmark))
        }

}