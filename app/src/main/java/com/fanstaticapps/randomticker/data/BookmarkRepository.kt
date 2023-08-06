package com.fanstaticapps.randomticker.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkRepository @Inject constructor(private val bookmarkDao: BookmarkDao) {

    fun getBookmarkById(id: Long): Flow<Bookmark> {
        return bookmarkDao.getById(id)
    }

    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks()
    }

    suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.delete(bookmark.id)
    }

    suspend fun insertOrUpdateBookmark(newBookmark: Bookmark): Long {
        return bookmarkDao.insert(newBookmark)
    }
}