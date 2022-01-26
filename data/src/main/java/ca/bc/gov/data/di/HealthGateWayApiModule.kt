package ca.bc.gov.data.di

import android.content.Context
import ca.bc.gov.data.R
import ca.bc.gov.data.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.remote.ImmunizationApi
import ca.bc.gov.data.remote.LaboratoryApi
import ca.bc.gov.data.remote.interceptor.CookiesInterceptor
import ca.bc.gov.data.remote.interceptor.QueueItInterceptor
import ca.bc.gov.data.remote.interceptor.ReceivedCookieInterceptor
import ca.bc.gov.data.remote.interceptor.UserAgentInterceptor
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Pinakin Kansara
 */
@InstallIn(SingletonComponent::class)
@Module
class HealthGateWayApiModule {

    @Provides
    fun provideCookieInterceptor(encryptedPreferenceStorage: EncryptedPreferenceStorage) =
        CookiesInterceptor(encryptedPreferenceStorage)

    @Provides
    fun providerReceivedCookieInterceptor(preferenceStorage: EncryptedPreferenceStorage) =
        ReceivedCookieInterceptor(preferenceStorage)

    @Provides
    fun providesQueueItInterceptor(
        preferenceStorage: EncryptedPreferenceStorage
    ) = QueueItInterceptor(preferenceStorage)

    @Provides
    fun providesUserAgentInterceptor(@ApplicationContext context: Context) = UserAgentInterceptor(
        context.getString(R.string.app_name), "1.0.0"
    )

    @Provides
    fun providesLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    fun providesOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        cookiesInterceptor: CookiesInterceptor,
        queueItInterceptor: QueueItInterceptor,
        receivedCookieInterceptor: ReceivedCookieInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(queueItInterceptor)
        .addInterceptor(cookiesInterceptor)
        .addInterceptor(receivedCookieInterceptor)
        .addInterceptor(loggingInterceptor)
        .hostnameVerifier { _, _ ->
            return@hostnameVerifier true
        }
        .build()

    @Provides
    fun providesJsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create(
        GsonBuilder()
            .setLenient()
            .create()
    )

    @Provides
    fun providesRetrofitClient(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(context.getString(R.string.base_url))
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()

    @Provides
    fun providesImmunizationApi(retrofit: Retrofit): ImmunizationApi =
        retrofit.create(ImmunizationApi::class.java)

    @Provides
    fun providesLaboratoryApi(retrofit: Retrofit): LaboratoryApi =
        retrofit.create(LaboratoryApi::class.java)
}
