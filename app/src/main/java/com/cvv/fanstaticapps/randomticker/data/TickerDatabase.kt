package com.cvv.fanstaticapps.randomticker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [(TickerData::class)], version = 1)
abstract class TickerDatabase : RoomDatabase() {

    abstract fun tickerDataDao(): TickerDataDao

    companion object {
        private var INSTANCE: TickerDatabase? = null

        private val migration1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tickerData ADD COLUMN name TEXT NOT NULL ")
            }
        }

        fun getInstance(context: Context): TickerDatabase? {
            if (INSTANCE == null) {
                synchronized(TickerDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            TickerDatabase::class.java, "random_ticker.db")
                            .addMigrations(migration1_2)
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }


}