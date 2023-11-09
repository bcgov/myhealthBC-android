package ca.bc.gov.data.di

import android.content.Context
import androidx.room.Room
import ca.bc.gov.data.BuildConfig
import ca.bc.gov.data.datasource.local.MyHealthDataBase
import ca.bc.gov.data.datasource.local.migration.ALL_MIGRATIONS
import ca.bc.gov.data.utils.RandomBytesGenerator
import ca.bc.gov.preference.EncryptedPreferenceStorage
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
        ).addMigrations(*ALL_MIGRATIONS)

        if (BuildConfig.FLAVOR != "dev" &&
            BuildConfig.FLAVOR != "stage" &&
            BuildConfig.FLAVOR != "mock"
        ) {
            builder.openHelperFactory(supportFactory)
        }
        builder.fallbackToDestructiveMigration()
        return builder.build()
    }
}
