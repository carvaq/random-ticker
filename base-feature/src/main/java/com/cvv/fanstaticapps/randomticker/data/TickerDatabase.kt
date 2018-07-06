package com.cvv.fanstaticapps.randomticker.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(TickerData::class)], version = 1)
abstract class TickerDatabase : RoomDatabase() {

    abstract fun tickerDataDao(): TickerDataDao

    companion object {
        private var INSTANCE: TickerDatabase? = null

        fun getInstance(context: Context): TickerDatabase? {
            if (INSTANCE == null) {
                synchronized(TickerDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            TickerDatabase::class.java, "ticker.db")
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