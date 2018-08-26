package com.fanstaticapps.randomticker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [(Bookmark::class)], version = 1)
abstract class TickerDatabase : RoomDatabase() {

    abstract fun tickerDataDao(): BookmarkDao

    companion object {
        private var INSTANCE: TickerDatabase? = null

        fun getInstance(context: Context): TickerDatabase? {
            if (INSTANCE == null) {
                synchronized(TickerDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            TickerDatabase::class.java, "tickerV2.db")
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