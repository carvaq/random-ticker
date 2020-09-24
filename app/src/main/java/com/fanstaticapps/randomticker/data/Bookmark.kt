package com.fanstaticapps.randomticker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by carla on 6/24/18
 */

@Entity(tableName = "bookmarks")
data class Bookmark(@PrimaryKey(autoGenerate = true) val id: Long?,
                    @ColumnInfo(name = "name") val name: String = "Random Ticker",
                    @ColumnInfo(name = "minimumMinutes") val minimumMinutes: Int,
                    @ColumnInfo(name = "minimumSeconds") val minimumSeconds: Int,
                    @ColumnInfo(name = "maximumMinutes") val maximumMinutes: Int,
                    @ColumnInfo(name = "maximumSeconds") val maximumSeconds: Int) {
    @Ignore
    constructor(name: String) : this(null, name, 0, 0, 5, 0)

    @Ignore
    constructor(name: String, minimumMinutes: Int, minimumSeconds: Int, maximumMinutes: Int, maximumSeconds: Int)
            : this(null, name, minimumMinutes, minimumSeconds, maximumMinutes, maximumSeconds)


}







