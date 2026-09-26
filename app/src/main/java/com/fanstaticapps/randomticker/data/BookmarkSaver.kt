package com.fanstaticapps.randomticker.data

import androidx.compose.runtime.saveable.mapSaver

object BookmarkSaver {
    private const val ID = "id"
    private const val NAME = "name"
    private const val MINIMUM_HOURS = "minimumHours"
    private const val MINIMUM_MINUTES = "minimumMinutes"
    private const val MINIMUM_SECONDS = "minimumSeconds"
    private const val MAXIMUM_HOURS = "maximumHours"
    private const val MAXIMUM_MINUTES = "maximumMinutes"
    private const val MAXIMUM_SECONDS = "maximumSeconds"
    private const val AUTO_REPEAT = "autoRepeat"
    private const val AUTO_REPEAT_INTERVAL = "autoRepeatInterval"
    private const val INTERVAL_END = "intervalEnd"
    private const val SOUND_URI = "soundUri"
    val saver =
        mapSaver(
            save = {
                mapOf(
                    ID to it.id,
                    NAME to it.name,
                    MINIMUM_HOURS to it.minimumHours,
                    MINIMUM_MINUTES to it.minimumMinutes,
                    MINIMUM_SECONDS to it.minimumSeconds,
                    MAXIMUM_HOURS to it.maximumHours,
                    MAXIMUM_MINUTES to it.maximumMinutes,
                    MAXIMUM_SECONDS to it.maximumSeconds,
                    AUTO_REPEAT to it.autoRepeat,
                    AUTO_REPEAT_INTERVAL to it.autoRepeatInterval,
                    INTERVAL_END to it.intervalEnd,
                    SOUND_URI to it.soundUri
                )
            },
            restore = {
                Bookmark(
                    id = it[ID] as Long,
                    name = it[NAME] as String,
                    minimumHours = it[MINIMUM_HOURS] as Int,
                    minimumMinutes = it[MINIMUM_MINUTES] as Int,
                    minimumSeconds = it[MINIMUM_SECONDS] as Int,
                    maximumHours = it[MAXIMUM_HOURS] as Int,
                    maximumMinutes = it[MAXIMUM_MINUTES] as Int,
                    maximumSeconds = it[MAXIMUM_SECONDS] as Int,
                    autoRepeat = it[AUTO_REPEAT] as Boolean,
                    autoRepeatInterval = it[AUTO_REPEAT_INTERVAL] as Long,
                    intervalEnd = it[INTERVAL_END] as Long,
                    soundUri = it[SOUND_URI] as String?
                )
            }
        )
}