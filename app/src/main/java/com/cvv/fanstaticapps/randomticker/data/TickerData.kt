package com.cvv.fanstaticapps.randomticker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by carla on 6/24/18
 */

@Entity(tableName = "tickerData")
data class TickerData(@PrimaryKey(autoGenerate = true) var id: Long?,
                      @ColumnInfo(name = "bookmarkName") var bookmarkName: String,
                      @ColumnInfo(name = "bookmarkPosition") var bookmarkPosition: Int,
                      @ColumnInfo(name = "minimumMinutes") var minimumMinutes: Int,
                      @ColumnInfo(name = "minimumSeconds") var minimumSeconds: Int,
                      @ColumnInfo(name = "maximumMinutes") var maximumMinutes: Int,
                      @ColumnInfo(name = "maximumSeconds") var maximumSeconds: Int) {
    constructor(name: String) : this(null, name, 0, 0, 0, 5, 0)
}







