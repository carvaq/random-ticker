package com.fanstaticapps.randomticker.injection

import android.app.Application
import androidx.room.Room
import com.fanstaticapps.randomticker.data.BookmarkDao
import com.fanstaticapps.randomticker.data.TickerDatabase
import com.fanstaticapps.randomticker.data.TickerDatabase.Companion.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataBaseModule {
    @Provides
    @Singleton
    fun provideTickerDatabase(application: Application): TickerDatabase {
        return Room.databaseBuilder(application, TickerDatabase::class.java, "tickerV2.db")
                .addMigrations(MIGRATION_1_2)
                .build()
    }

    @Provides
    @Singleton
    fun provideBookmarksDao(database: TickerDatabase): BookmarkDao {
        return database.tickerDataDao()
    }
}