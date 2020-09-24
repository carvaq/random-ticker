package com.fanstaticapps.randomticker.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.data.Bookmark
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_bookmark.*
import javax.inject.Inject

@AndroidEntryPoint
class BookmarkDialog : BottomSheetDialogFragment() {

    @Inject
    lateinit var userPreferences: UserPreferences

    private lateinit var viewModel: BookmarksViewModel
    private lateinit var adapter: BookmarkAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (userPreferences.darkTheme) {
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Dialog_Dark)
        } else {
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Dialog_Light)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel = ViewModelProvider(this).get(BookmarksViewModel::class.java)

        viewModel.allBookmarks.observe(viewLifecycleOwner, { list ->
            val allItems = listOf(Bookmark("Random Ticker")).plus(list)

            adapter.setData(allItems)
        })
    }

    private fun setupRecyclerView() {
        adapter = BookmarkAdapter(requireContext(),
                { bookmark ->
                    if (activity is BookmarkSelector) {
                        (activity as BookmarkSelector).onBookmarkSelected(bookmark)
                    }
                    dismiss()
                },
                { bookmark ->
                    viewModel.deleteBookmark(bookmark)
                })

        rvBookmarks.layoutManager = LinearLayoutManager(requireContext())
        rvBookmarks.adapter = adapter
    }

    interface BookmarkSelector {
        fun onBookmarkSelected(bookmark: Bookmark?)
    }
}

