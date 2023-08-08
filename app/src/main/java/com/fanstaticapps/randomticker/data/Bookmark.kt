package com.fanstaticapps.randomticker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = NOT_SET_VALUE,
    @ColumnInfo(name = "name") val name: String = "Random Ticker",
    @ColumnInfo(name = "minimumHours") val minimumHours: Int = 0,
    @ColumnInfo(name = "minimumMinutes") val minimumMinutes: Int = 0,
    @ColumnInfo(name = "minimumSeconds") val minimumSeconds: Int = 0,
    @ColumnInfo(name = "maximumHours") val maximumHours: Int = 0,
    @ColumnInfo(name = "maximumMinutes") val maximumMinutes: Int = 5,
    @ColumnInfo(name = "maximumSeconds") val maximumSeconds: Int = 0,
    @ColumnInfo(name = "autoRepeat") val autoRepeat: Boolean = false,
    @ColumnInfo(name = "autoRepeatInterval") val autoRepeatInterval: Long = DEFAULT_AUTO_REPEAT_INTERVAL,
    @ColumnInfo(name = "intervalEnd") val intervalEnd: Long = NOT_SET_VALUE,
) {
    fun reset(): Bookmark = copy(intervalEnd = NOT_SET_VALUE)
    fun isIdSet() = id != NOT_SET_VALUE

    @Ignore
    constructor(name: String) : this(NOT_SET_VALUE, name, 0, 0, 0, 0, 5, 0, false)

    @Ignore
    constructor(
        id: Long?,
        name: String,
        minimum: Boundary,
        maximum: Boundary,
        autoRepeat: Boolean,
        intervalEnd: Long
    ) : this(
        id ?: NOT_SET_VALUE,
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

    @Ignore
    val min = Boundary(minimumHours, minimumMinutes, minimumSeconds)

    @Ignore
    val max = Boundary(maximumHours, maximumMinutes, maximumSeconds)

    fun klaxonChannelId() = "$name-KLAXON"
    fun klaxonNotificationId() = Int.MAX_VALUE - id.toInt()
    fun runningChannelId() = "$name-RUNNING"
    fun runningNotificationId() = id.toInt()

    private companion object {
        const val NOT_SET_VALUE: Long = 0
        const val DEFAULT_AUTO_REPEAT_INTERVAL: Long = 200
    }
}







