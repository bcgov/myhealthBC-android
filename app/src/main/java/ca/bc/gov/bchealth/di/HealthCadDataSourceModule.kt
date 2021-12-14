package ca.bc.gov.bchealth.di

import ca.bc.gov.bchealth.datasource.DataStoreRepo
import ca.bc.gov.bchealth.utils.RandomBytesGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun providesRandomBytes(dataStoreRepo: DataStoreRepo) =
        RandomBytesGenerator(dataStoreRepo)
}
