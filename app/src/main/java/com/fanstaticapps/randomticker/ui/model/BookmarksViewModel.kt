package com.fanstaticapps.randomticker.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.fanstaticapps.randomticker.data.Bookmark
import com.fanstaticapps.randomticker.data.BookmarkDao

class BookmarksViewModel : ViewModel() {
    private var liveData: LiveData<List<Bookmark>>? = null
    fun getAllBookmarks(bookmarkDao: BookmarkDao): LiveData<List<Bookmark>> {
        if (liveData == null) {
            liveData = bookmarkDao.getAllBookmarks()
        }
        return liveData!!
    }
}