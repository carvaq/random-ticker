package com.fanstaticapps.randomticker.data

import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val bookmarkDao: BookmarkDao) {

    fun getBookmarkById(id: Long): Flow<Bookmark> {
        return bookmarkDao.getById(id)
    }

    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks()
    }

    suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.delete(bookmark.id)
    }

    fun insertOrUpdateBookmark(newBookmark: Bookmark): Long {
        return bookmarkDao.insert(newBookmark)
    }
    fun bulkUpdate(bookmarks: List<Bookmark>) {
        bookmarkDao.insertAll(bookmarks)
    }
}