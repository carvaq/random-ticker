package com.fanstaticapps.randomticker.ui.cancel

import android.content.Intent
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.extensions.EXTRA_BOOKMARK_ID
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class CancelActivityTest {
    private val bookmarkService: BookmarkService = mockk(relaxed = true)

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            modules(
                module {
                    single { bookmarkService }
                    viewModel { CancelViewModel(get()) }
                }
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `launching CancelActivity with bookmark ID cancels ticker and finishes activity`() {
        val intent =
            Intent(RuntimeEnvironment.getApplication(), CancelActivity::class.java).apply {
                putExtra(EXTRA_BOOKMARK_ID, 55L)
            }

        val controller = Robolectric.buildActivity(CancelActivity::class.java, intent)
        controller.create()

        verify { bookmarkService.cancel(55L) }
        assertTrue(controller.get().isFinishing)
    }
}