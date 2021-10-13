package ca.bc.gov.bchealth.di

import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.services.ImmunizationServices
import ca.bc.gov.bchealth.utils.SHCDecoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * [RepositoryModule]
 *
 * @author Pinakin Kansara
 */
@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideCardRepository(
        localDataSource: LocalDataSource,
        shcDecoder: SHCDecoder,
        immunizationServices: ImmunizationServices
    ) =
        CardRepository(localDataSource, shcDecoder, immunizationServices)
}
