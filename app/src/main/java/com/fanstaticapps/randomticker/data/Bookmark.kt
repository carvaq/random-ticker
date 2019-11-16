package com.fanstaticapps.randomticker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by carla on 6/24/18
 */

@Entity(tableName = "bookmarks")
data class Bookmark(@PrimaryKey(autoGenerate = true) var id: Long?,
                    @ColumnInfo(name = "name") var name: String = "Random Ticker",
                    @ColumnInfo(name = "minimumMinutes") var minimumMinutes: Int,
                    @ColumnInfo(name = "minimumSeconds") var minimumSeconds: Int,
                    @ColumnInfo(name = "maximumMinutes") var maximumMinutes: Int,
                    @ColumnInfo(name = "maximumSeconds") var maximumSeconds: Int) {
    @Ignore constructor(name: String) : this(null, name, 0, 0, 5, 0)
}







