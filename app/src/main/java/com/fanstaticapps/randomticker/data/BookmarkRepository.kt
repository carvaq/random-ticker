package com.fanstaticapps.randomticker.data

import androidx.lifecycle.LiveData
import javax.inject.Inject

class BookmarkRepository @Inject constructor(private val bookmarkDao: BookmarkDao) {

    suspend fun getBookmarkById(id: Long): Bookmark? {
        return bookmarkDao.getById(id)
    }

    fun getAllBookmarks(): LiveData<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks()
    }

    suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.delete(bookmark.id)
    }

    suspend fun insertOrUpdateBookmark(newBookmark: Bookmark): Long {
        return bookmarkDao.insert(newBookmark)
    }
}