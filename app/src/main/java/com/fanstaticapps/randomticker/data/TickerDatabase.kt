package com.fanstaticapps.randomticker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Bookmark::class], version = 4)
abstract class TickerDatabase : RoomDatabase() {

    abstract fun tickerDataDao(): BookmarkDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE bookmarks ADD COLUMN minimumHours INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE bookmarks ADD COLUMN maximumHours INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE bookmarks ADD COLUMN autoRepeat INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE temp_bookmarks (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `minimumHours` INTEGER NOT NULL, `minimumMinutes` INTEGER NOT NULL, `minimumSeconds` INTEGER NOT NULL, `maximumHours` INTEGER NOT NULL, `maximumMinutes` INTEGER NOT NULL, `maximumSeconds` INTEGER NOT NULL, `autoRepeat` INTEGER NOT NULL, `autoRepeatInterval` INTEGER NOT NULL DEFAULT 2000, `intervalEnd` INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("INSERT INTO temp_bookmarks (name, minimumHours, minimumMinutes, minimumSeconds, maximumHours, maximumMinutes, maximumSeconds, autoRepeat) SELECT name, minimumHours, minimumMinutes, minimumSeconds, maximumHours, maximumMinutes, maximumSeconds, autoRepeat FROM bookmarks")
                db.execSQL("DROP TABLE bookmarks")
                db.execSQL("ALTER TABLE temp_bookmarks RENAME TO bookmarks")
            }
        }
        val MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
    }

}