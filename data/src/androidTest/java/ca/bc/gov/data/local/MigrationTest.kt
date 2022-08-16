package ca.bc.gov.data.local

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ca.bc.gov.data.datasource.local.MyHealthDataBase
import ca.bc.gov.data.datasource.local.migration.MIGRATION_1_2
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val migrationTestDb = "migration-test"
    private val migrations = arrayOf(
        MIGRATION_1_2,
    )

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MyHealthDataBase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun validate_all_migrations() {
        helper.createDatabase(migrationTestDb, 1)
            .close()
        
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MyHealthDataBase::class.java,
            migrationTestDb
        ).addMigrations(*migrations).build().apply {
            openHelper.writableDatabase.close()
        }
    }
}