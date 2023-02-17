package ca.bc.gov.data.di

import android.content.Context
import androidx.room.Room
import ca.bc.gov.data.BuildConfig
import ca.bc.gov.data.datasource.local.MyHealthDataBase
import ca.bc.gov.data.datasource.local.migration.MIGRATION_1_2
import ca.bc.gov.data.datasource.local.migration.MIGRATION_2_3
import ca.bc.gov.data.datasource.local.migration.MIGRATION_3_4
import ca.bc.gov.data.datasource.local.migration.MIGRATION_4_5
import ca.bc.gov.data.datasource.local.migration.MIGRATION_5_6
import ca.bc.gov.data.datasource.local.migration.MIGRATION_6_7
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.utils.RandomBytesGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

/**
 * @author Pinakin Kansara
 */
@InstallIn(SingletonComponent::class)
@Module
class MyHealthDataBaseModule {

    @Provides
    @Singleton
    fun providesRandomByteGenerator(preferenceStorage: EncryptedPreferenceStorage) =
        RandomBytesGenerator(preferenceStorage)

    @Provides
    fun providesSupportHelperFactory(randomBytesGenerator: RandomBytesGenerator) =
        SupportFactory(randomBytesGenerator.getSecureRandom())

    @Provides
    @Singleton
    fun providesMyHealthDataBase(
        @ApplicationContext context: Context,
        supportFactory: SupportFactory
    ): MyHealthDataBase {

        val builder = Room.databaseBuilder(
            context,
            MyHealthDataBase::class.java,
            "my_health_db"
        ).addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7
        )

        if (BuildConfig.FLAVOR != "dev" &&
            BuildConfig.FLAVOR != "stage" &&
            BuildConfig.FLAVOR != "mock"
        ) {
            builder.openHelperFactory(supportFactory)
        }
        return builder.build()
    }
}
