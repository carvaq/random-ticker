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
    fun insert(bookmark: Bookmark): Long

    @Query("SELECT * from bookmarks WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<Bookmark>

    @Query("DELETE from bookmarks WHERE id = :id")
    suspend fun delete(id: Long?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bookmarks: List<Bookmark>)
}