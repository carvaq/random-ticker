package com.fanstaticapps.randomticker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by carvaq on 24/6/18
 */

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "name") val name: String = "Random Ticker",
    @ColumnInfo(name = "minimumHours") val minimumHours: Int = 0,
    @ColumnInfo(name = "minimumMinutes") val minimumMinutes: Int = 0,
    @ColumnInfo(name = "minimumSeconds") val minimumSeconds: Int = 0,
    @ColumnInfo(name = "maximumHours") val maximumHours: Int = 0,
    @ColumnInfo(name = "maximumMinutes") val maximumMinutes: Int = 5,
    @ColumnInfo(name = "maximumSeconds") val maximumSeconds: Int = 0,
    @ColumnInfo(name = "autoRepeat") val autoRepeat: Boolean = false,
    @ColumnInfo(name = "intervalEnd") val intervalEnd: Long = 0
) {
    @Ignore
    constructor(name: String) : this(null, name, 0, 0, 0, 0, 5, 0, false)

    @Ignore
    constructor(
        id: Long?,
        name: String,
        minimum: IntervalDefinition,
        maximum: IntervalDefinition,
        autoRepeat: Boolean,
        intervalEnd: Long
    ) : this(
        id,
        name,
        minimum.hours,
        minimum.minutes,
        minimum.seconds,
        maximum.hours,
        maximum.minutes,
        maximum.seconds,
        autoRepeat,
        intervalEnd
    )
}







