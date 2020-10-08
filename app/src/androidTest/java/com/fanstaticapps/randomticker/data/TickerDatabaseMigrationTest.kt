package com.fanstaticapps.randomticker.data

import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4ClassRunner::class)
class TickerDatabaseMigrationTest {
    companion object {
        private const val TEST_DB_NAME = "migration-test"
        private val ALL_MIGRATIONS = arrayOf(TickerDatabase.MIGRATION_1_2, TickerDatabase.MIGRATION_2_3)
    }

    @get:Rule
    val testHelper: MigrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            TickerDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        createEarliestDatabase()

        Room.databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                TickerDatabase::class.java,
                TEST_DB_NAME
        )
                .addMigrations(*ALL_MIGRATIONS).build().apply {
                    openHelper.writableDatabase
                    val bookmark = runBlocking {
                        tickerDataDao().getById(1)
                    }
                    assert(bookmark != null)
                    bookmark!!.run {
                        assertThat(name).isEqualTo("TickerLife")
                        assertThat(maximumHours).isEqualTo(0)
                        assertThat(minimumHours).isEqualTo(0)
                        assertThat(autoRepeat).isFalse()
                    }

                    close()
                }
    }


    private fun createEarliestDatabase() {
        testHelper.createDatabase(TEST_DB_NAME, 1).apply {
            insert("bookmarks", SQLiteDatabase.CONFLICT_FAIL, contentValuesOf(
                    "id" to 1,
                    "name" to "TickerLife",
                    "minimumMinutes" to 9,
                    "minimumSeconds" to 40,
                    "maximumMinutes" to 15,
                    "maximumSeconds" to 48,
            ))
            close()
        }
    }
}