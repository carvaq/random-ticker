package com.fanstaticapps.randomticker.data

import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4ClassRunner::class)
class TickerDatabaseMigrationTest {
    @get:Rule
    val testHelper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TickerDatabase::class.java,
        listOf(),
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
            .addMigrations(*TickerDatabase.MIGRATIONS).build().apply {
                openHelper.writableDatabase
                val bookmark = runBlocking {
                    tickerDataDao().getById(1).firstOrNull()
                }
                assert(bookmark != null)
                bookmark?.run {
                    assertThat(name).isEqualTo("TickerLife")
                    assertThat(maximumHours).isEqualTo(0)
                    assertThat(minimumHours).isEqualTo(0)
                    assertThat(autoRepeat).isFalse()
                    assertThat(autoRepeatInterval).isEqualTo(2000)
                    assertThat(intervalEnd).isEqualTo(0)
                }

                close()
            }
    }

    private fun createEarliestDatabase() {
        testHelper.createDatabase(TEST_DB_NAME, 1).apply {
            insert(
                "bookmarks", SQLiteDatabase.CONFLICT_FAIL, contentValuesOf(
                    "id" to 1,
                    "name" to "TickerLife",
                    "minimumMinutes" to 9,
                    "minimumSeconds" to 40,
                    "maximumMinutes" to 15,
                    "maximumSeconds" to 48,
                )
            )
            close()
        }
    }


    companion object {
        private const val TEST_DB_NAME = "migration-test"
    }
}