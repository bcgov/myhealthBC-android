package ca.bc.gov.bchealth.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
