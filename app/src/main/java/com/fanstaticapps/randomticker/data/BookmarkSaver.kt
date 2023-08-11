package com.fanstaticapps.randomticker.data

import androidx.compose.runtime.saveable.mapSaver

object BookmarkSaver {

    private const val id = "id"
    private const val name = "name"
    private const val minimumHours = "minimumHours"
    private const val minimumMinutes = "minimumMinutes"
    private const val minimumSeconds = "minimumSeconds"
    private const val maximumHours = "maximumHours"
    private const val maximumMinutes = "maximumMinutes"
    private const val maximumSeconds = "maximumSeconds"
    private const val autoRepeat = "autoRepeat"
    private const val autoRepeatInterval = "autoRepeatInterval"
    private const val intervalEnd = "intervalEnd"
    val saver = mapSaver(save = {
        mapOf(
            id to it.id,
            name to it.name,
            minimumHours to it.minimumHours,
            minimumMinutes to it.minimumMinutes,
            minimumSeconds to it.minimumSeconds,
            maximumHours to it.maximumHours,
            maximumMinutes to it.maximumMinutes,
            maximumSeconds to it.maximumSeconds,
            autoRepeat to it.autoRepeat,
            autoRepeatInterval to it.autoRepeatInterval,
            intervalEnd to it.intervalEnd
        )
    },
        restore = {
            Bookmark(
                it[id] as Long,
                it[name] as String,
                it[minimumHours] as Int,
                it[minimumMinutes] as Int,
                it[minimumSeconds] as Int,
                it[maximumHours] as Int,
                it[maximumMinutes] as Int,
                it[maximumSeconds] as Int,
                it[autoRepeat] as Boolean,
                it[autoRepeatInterval] as Long,
                it[intervalEnd] as Long
            )
        })
}