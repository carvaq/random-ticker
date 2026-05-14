package com.fanstaticapps.randomticker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("SELECT * from bookmarks")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: Bookmark): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmarks: List<Bookmark>)

    @Query("SELECT * from bookmarks WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<Bookmark>

    @Query("SELECT * from bookmarks WHERE id = :id LIMIT 1")
    suspend fun getByIdOnce(id: Long): Bookmark?

    @Query("SELECT * from bookmarks")
    suspend fun getAllBookmarksOnce(): List<Bookmark>

    @Query("DELETE from bookmarks WHERE id = :id")
    suspend fun delete(id: Long?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bookmarks: List<Bookmark>)
}