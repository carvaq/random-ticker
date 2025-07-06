package com.fanstaticapps.randomticker.data

import androidx.core.net.toUri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlin.random.Random
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = NOT_SET_VALUE,
    val name: String = "Random Ticker",
    val minimumHours: Int = 0,
    val minimumMinutes: Int = 0,
    val minimumSeconds: Int = 0,
    val maximumHours: Int = 0,
    val maximumMinutes: Int = 5,
    val maximumSeconds: Int = 0,
    val autoRepeat: Boolean = false,
    val autoRepeatInterval: Long = DEFAULT_AUTO_REPEAT_INTERVAL,
    val intervalEnd: Long = NOT_SET_VALUE,
    val soundUri: String? = null,
) {
    fun reset(): Bookmark = copy(intervalEnd = NOT_SET_VALUE)

    @Ignore
    constructor(name: String) : this(NOT_SET_VALUE, name, 0, 0, 0, 0, 5, 0, false)

    @Ignore
    private val requestCodeGenerator = Random(id)

    @Ignore
    val min = minimumHours.hours + minimumMinutes.minutes + minimumSeconds.seconds

    @Ignore
    val max = maximumHours.hours + maximumMinutes.minutes + maximumSeconds.seconds

    @Ignore
    val notificationChannelId = "KLAXON-${soundUri?.toUri()?.lastPathSegment ?: "default"}"

    @Ignore
    val klaxonNotificationId = Int.MAX_VALUE - id.toInt()

    @Ignore
    val runningNotificationId = id.toInt()

    @Ignore
    val openAppRequestCode = requestCodeGenerator.nextInt()

    @Ignore
    val cancelActionRequestCode = requestCodeGenerator.nextInt()

    @Ignore
    val repeatReceiverRequestCode = requestCodeGenerator.nextInt()

    @Ignore
    val klaxonActivityRequestCode = requestCodeGenerator.nextInt()

    companion object {
        const val NOT_SET_VALUE: Long = 0
        const val DEFAULT_AUTO_REPEAT_INTERVAL: Long = 5000
    }
}







