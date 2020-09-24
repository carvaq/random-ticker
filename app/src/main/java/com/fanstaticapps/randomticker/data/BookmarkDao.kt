package com.fanstaticapps.randomticker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface BookmarkDao {

    @Query("SELECT * from bookmarks")
    fun getAll(): List<Bookmark>

    @Query("SELECT * from bookmarks")
    fun getAllBookmarks(): LiveData<List<Bookmark>>

    @Insert(onConflict = REPLACE)
    fun insert(bookmark: Bookmark)

    @Query("SELECT * from bookmarks WHERE id = :id LIMIT 1")
    fun getById(id: Long): LiveData<Bookmark>

    @Query("DELETE from bookmarks WHERE id = :id")
    fun delete(id: Long?)

}