package com.fanstaticapps.randomticker.injection

import android.app.Application
import androidx.room.Room
import com.fanstaticapps.randomticker.data.BookmarkDao
import com.fanstaticapps.randomticker.data.TickerDatabase
import dagger.Provides
import javax.inject.Singleton

object DataBaseModule {
    @Provides
    @Singleton
    fun providePokemonDB(application: Application): TickerDatabase {
        return Room.databaseBuilder(application, TickerDatabase::class.java, "tickerV2.db")
                .build()
    }

    @Provides
    @Singleton
    fun providePokeDao(database: TickerDatabase): BookmarkDao {
        return database.tickerDataDao()
    }
}