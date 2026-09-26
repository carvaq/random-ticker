package com.fanstaticapps.randomticker.ui.cancel

import com.fanstaticapps.randomticker.data.BookmarkService
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class CancelViewModelTest {
    private val bookmarkService: BookmarkService = mockk(relaxed = true)
    private val viewModel = CancelViewModel(bookmarkService)

    @Test
    fun `cancelTicker with non-null ID calls cancel on service`() {
        viewModel.cancelTicker(123L)
        verify { bookmarkService.cancel(123L) }
    }

    @Test
    fun `cancelTicker with null ID does not call cancel on service`() {
        viewModel.cancelTicker(null)
        verify(exactly = 0) { bookmarkService.cancel(any()) }
    }
}