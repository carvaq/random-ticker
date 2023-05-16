package com.fanstaticapps.randomticker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookmarkDao {

    @Query("SELECT * from bookmarks")
    fun getAllBookmarks(): LiveData<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: Bookmark): Long

    @Query("SELECT * from bookmarks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Bookmark?

    @Query("DELETE from bookmarks WHERE id = :id")
    suspend fun delete(id: Long?)

}