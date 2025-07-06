package com.fanstaticapps.randomticker

import android.app.Application
import androidx.room.Room
import com.fanstaticapps.randomticker.data.BookmarkRepository
import com.fanstaticapps.randomticker.data.BookmarkService
import com.fanstaticapps.randomticker.data.TickerDatabase
import com.fanstaticapps.randomticker.data.TickerDatabase.Companion.MIGRATIONS
import com.fanstaticapps.randomticker.helper.AlarmCoordinator
import com.fanstaticapps.randomticker.helper.MigrationService
import com.fanstaticapps.randomticker.helper.NotificationCoordinator
import com.fanstaticapps.randomticker.ui.cancel.CancelViewModel
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonViewModel
import com.fanstaticapps.randomticker.ui.main.MainViewModel
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber
import timber.log.Timber.DebugTree

class TickerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        startKoin {
            androidLogger()
            androidContext(this@TickerApplication)
            modules(modules)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private val modules = listOf(
        module(createdAtStart = true) {
            single {
                Room.databaseBuilder(androidContext(), TickerDatabase::class.java, "tickerV2.db")
                    .addMigrations(*MIGRATIONS)
                    .build()
            }
            single { get<TickerDatabase>().tickerDataDao() }
            factoryOf(::BookmarkRepository)
        },

        module {
            factoryOf(::NotificationCoordinator)
            factoryOf(::AlarmCoordinator)
        },
        module { factory { BookmarkService(get(), get(), get()) } },
        module { factoryOf(::MigrationService) },
        module {
            viewModel { new(::MainViewModel) }
            viewModel { new(::KlaxonViewModel) }
            viewModel { new(::CancelViewModel) }
        }
    )
}