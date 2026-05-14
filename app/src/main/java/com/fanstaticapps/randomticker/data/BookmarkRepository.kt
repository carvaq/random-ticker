package com.fanstaticapps.randomticker.data

import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val bookmarkDao: BookmarkDao) {

    fun getBookmarkById(id: Long): Flow<Bookmark> {
        return bookmarkDao.getById(id)
    }

    suspend fun getBookmarkByIdOnce(id: Long): Bookmark? {
        return bookmarkDao.getByIdOnce(id)
    }

    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks()
    }

    suspend fun getAllBookmarksOnce(): List<Bookmark> {
        return bookmarkDao.getAllBookmarksOnce()
    }

    suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.delete(bookmark.id)
    }

    suspend fun insertOrUpdateBookmark(newBookmark: Bookmark): Long {
        return bookmarkDao.insert(newBookmark)
    }

    suspend fun bulkUpdate(bookmarks: List<Bookmark>) {
        bookmarkDao.insert(bookmarks)
    }
}