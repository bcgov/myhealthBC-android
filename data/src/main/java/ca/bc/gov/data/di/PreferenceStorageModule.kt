package ca.bc.gov.data.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author Pinakin Kansara
 */
@InstallIn(SingletonComponent::class)
@Module
class PreferenceStorageModule {

    @Provides
    fun provideMasterKey(@ApplicationContext context: Context) = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    @Provides
    @Singleton
    fun provideEncryptedPreferenceStorage(@ApplicationContext context: Context, key: MasterKey) =
        EncryptedSharedPreferences.create(
            context,
            "_preferences",
            key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

}