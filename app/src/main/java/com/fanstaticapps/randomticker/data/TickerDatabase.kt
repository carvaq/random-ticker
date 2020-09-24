package com.fanstaticapps.randomticker.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Bookmark::class], version = 1)
abstract class TickerDatabase : RoomDatabase() {

    abstract fun tickerDataDao(): BookmarkDao

}