package ca.bc.gov.bchealth.di

import android.content.Context
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.services.ImmunizationServices
import ca.bc.gov.bchealth.ui.login.AuthManagerRepo
import ca.bc.gov.bchealth.utils.SHCDecoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideAuthManagerRepository(
        @ApplicationContext appContext: Context,
        dataStoreRepo: DataStoreRepo
    ) =
        AuthManagerRepo(appContext, dataStoreRepo)
}
