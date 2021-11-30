package ca.bc.gov.bchealth.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ca.bc.gov.bchealth.data.local.BcVaccineCardDataBase
import ca.bc.gov.bchealth.datasource.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * [HealthCadDataSourceModule]
 *
 * @author Pinakin Kansara
 */
@Module
@InstallIn(SingletonComponent::class)
class HealthCadDataSourceModule {

    @Provides
    @Singleton
    fun providesDataBase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        BcVaccineCardDataBase::class.java,
        "bc_vaccine_card_db"
    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideLocalDataSource(dataBase: BcVaccineCardDataBase) = LocalDataSource(dataBase)

    /*
    * HealthCard entity has been replaced by HealthCardV2
    * */
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {

            database.execSQL(
                "ALTER TABLE" +
                    " `health_card`" +
                    "ADD COLUMN federalPass TEXT "
            )
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS " +
                    "`covid_test_results` " +
                    "(reportId TEXT PRIMARY KEY NOT NULL," +
                    "patientDisplayName TEXT NOT NULL," +
                    "lab TEXT NOT NULL," +
                    "collectionDateTime INTEGER NOT NULL," +
                    "resultDateTime INTEGER NOT NULL," +
                    "testName TEXT NOT NULL," +
                    "testType TEXT NOT NULL," +
                    "testStatus TEXT NOT NULL," +
                    "testOutcome TEXT NOT NULL," +
                    "resultTitle TEXT NOT NULL," +
                    "resultDescription TEXT NOT NULL," +
                    "resultLink TEXT NOT NULL," +
                    "userId TEXT NOT NULL)"
            )
        }
    }
}
