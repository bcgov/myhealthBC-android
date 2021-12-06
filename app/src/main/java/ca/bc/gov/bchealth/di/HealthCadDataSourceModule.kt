package ca.bc.gov.bchealth.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.sqlite.db.SupportSQLiteDatabase
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.data.local.BcVaccineCardDataBase
import ca.bc.gov.bchealth.datasource.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
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
    fun providesDataBase(@ApplicationContext context: Context): BcVaccineCardDataBase {

        // TODO: 06/12/21 Logic to be place to get already generated random key 
        val supportFactory = SupportFactory(generateRandomKey())

        return Room.databaseBuilder(
            context,
            BcVaccineCardDataBase::class.java,
            "bc_vaccine_card_db"
        )
            .openHelperFactory(supportFactory)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()

    }

    private fun generateRandomKey(): ByteArray =
        ByteArray(32).apply {
            SecureRandom.getInstanceStrong().nextBytes(this)
        }

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

    @Provides
    @Singleton
    fun provideEncryptedPreferences(@ApplicationContext context: Context): SharedPreferences {

        // Step 1: Create or retrieve the Master Key for encryption/decryption
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Step 2: Initialize/open an instance of EncryptedSharedPreferences
        return EncryptedSharedPreferences.create(
            context,
            BuildConfig.APPLICATION_ID + "_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
