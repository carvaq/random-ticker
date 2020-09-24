package com.fanstaticapps.randomticker.data

import androidx.lifecycle.LiveData
import javax.inject.Inject

class BookmarkRepository @Inject constructor(private val bookmarkDao: BookmarkDao) {

    fun getBookmarkById(id: Long): LiveData<Bookmark> {
        return bookmarkDao.getById(id)
    }

    fun getAllBookmarks(): LiveData<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks()
    }

    fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.delete(bookmark.id)
    }
}