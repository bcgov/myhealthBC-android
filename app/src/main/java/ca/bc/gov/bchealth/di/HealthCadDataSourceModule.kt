package ca.bc.gov.bchealth.di

import android.content.Context
import androidx.room.Room
import ca.bc.gov.bchealth.data.local.BcVaccineCardDataBase
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.utils.RandomBytesGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
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
    fun providesRandomBytes(dataStoreRepo: DataStoreRepo) =
        RandomBytesGenerator(dataStoreRepo)

    @Provides
    @Singleton
    fun providesDataBase(
        @ApplicationContext context: Context,
        randomBytesGenerator: RandomBytesGenerator
    ): BcVaccineCardDataBase {

        val supportFactory = SupportFactory(randomBytesGenerator.getSecureRandom())

        return Room.databaseBuilder(
            context,
            BcVaccineCardDataBase::class.java,
            "bc_vaccine_card_db"
        )
            .openHelperFactory(supportFactory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(dataBase: BcVaccineCardDataBase) = LocalDataSource(dataBase)
}
