package app.surgo.core

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.surgo.core.room.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(DATABASE_NAME, 1).apply {
            close()
        }
    }

    /*
    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(DATABASE_NAME, 1).apply {
            execSQL(...)
            close()
        }

        db = helper.runMigrationsAndValidate(DATABASE_NAME, 2, true, MIGRATION_1_2)
    }
     */

    companion object {
        private const val DATABASE_NAME: String = "testdb"

        // Array of all migrations
        //private val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
    }
}
