package com.fanstaticapps.randomticker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Bookmark::class], version = 2)
abstract class TickerDatabase : RoomDatabase() {

    abstract fun tickerDataDao(): BookmarkDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE bookmarks ADD COLUMN minimumHours INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE bookmarks ADD COLUMN maximumHours INTEGER NOT NULL DEFAULT 0")
            }
        }
    }

}