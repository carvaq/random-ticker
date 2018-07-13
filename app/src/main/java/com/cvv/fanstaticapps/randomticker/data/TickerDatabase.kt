package com.cvv.fanstaticapps.randomticker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


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