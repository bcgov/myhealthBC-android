package ca.bc.gov.repository.di

import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.repository.analytics.AnalyticsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author Pinakin Kansara
 */
@InstallIn(SingletonComponent::class)
@Module
class AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalyticsRepository(preferenceStorage: EncryptedPreferenceStorage) =
        AnalyticsRepository(preferenceStorage)
}
