package com.fanstaticapps.randomticker.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.databinding.DialogBookmarkBinding
import com.fanstaticapps.randomticker.extensions.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BookmarkDialog : BottomSheetDialogFragment() {

    @Inject
    lateinit var tickerPreferences: TickerPreferences

    private val viewModel: BookmarksViewModel by viewModels()

    private val binding by viewBinding(DialogBookmarkBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = BookmarkAdapter(requireContext(),
            { bookmark ->
                if (bookmark.id == null) {
                    viewModel.createBookmark(bookmark)
                } else {
                    viewModel.selectBookmark(bookmark)
                }
                dismiss()
            },
            { bookmark ->
                viewModel.deleteBookmark(bookmark)
            })

        binding.rvBookmarks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookmarks.adapter = adapter

        viewModel.allBookmarks.observe(viewLifecycleOwner, { bookmarks ->
            val allItems = listOf(Bookmark("Random Ticker")).plus(bookmarks)
            adapter.updateBookmarks(allItems)
        })
    }
}