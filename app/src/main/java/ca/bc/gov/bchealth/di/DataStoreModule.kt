package ca.bc.gov.bchealth.di

import android.content.SharedPreferences
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * [DataStoreModule]
 *
 * @author amit metri
 */
@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    @Singleton
    fun providesDataStore(sharedPreferences: SharedPreferences) =
        DataStoreRepo(sharedPreferences)
}
