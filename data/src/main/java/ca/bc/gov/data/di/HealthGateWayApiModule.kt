package ca.bc.gov.data.di

import android.content.Context
import ca.bc.gov.data.BuildConfig
import ca.bc.gov.data.R
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.datasource.remote.api.HealthGatewayMobileConfigApi
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPublicApi
import ca.bc.gov.data.datasource.remote.interceptor.CookiesInterceptor
import ca.bc.gov.data.datasource.remote.interceptor.HostSelectionInterceptor
import ca.bc.gov.data.datasource.remote.interceptor.MockInterceptor
import ca.bc.gov.data.datasource.remote.interceptor.NetworkConnectionInterceptor
import ca.bc.gov.data.datasource.remote.interceptor.QueueItInterceptor
import ca.bc.gov.data.datasource.remote.interceptor.ReceivedCookieInterceptor
import ca.bc.gov.data.datasource.remote.interceptor.UserAgentInterceptor
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
import java.util.concurrent.TimeUnit

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
    fun providesMockInterceptor(@ApplicationContext context: Context) =
        MockInterceptor(context)

    @Provides
    fun providesNetworkConnectionInterceptor(@ApplicationContext context: Context) =
        NetworkConnectionInterceptor(context)

    @Provides
    fun providesOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        cookiesInterceptor: CookiesInterceptor,
        queueItInterceptor: QueueItInterceptor,
        receivedCookieInterceptor: ReceivedCookieInterceptor,
        mockInterceptor: MockInterceptor,
        hostSelectionInterceptor: HostSelectionInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(loggingInterceptor)
            .hostnameVerifier { _, _ ->
                return@hostnameVerifier true
            }
        if (BuildConfig.FLAVOR == "mock") {
            okHttpClient
                .addInterceptor(mockInterceptor)
        } else {
            okHttpClient
                .addInterceptor(networkConnectionInterceptor)
                .addInterceptor(hostSelectionInterceptor)
                .addInterceptor(queueItInterceptor)
                .addInterceptor(cookiesInterceptor)
                .addInterceptor(receivedCookieInterceptor)
        }
        return okHttpClient.build()
    }

    @Provides
    @MobileConfigOkHttp
    fun providesMobileConfigOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        mockInterceptor: MockInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor,
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(loggingInterceptor)
            .hostnameVerifier { _, _ ->
                return@hostnameVerifier true
            }
            .addInterceptor(networkConnectionInterceptor)
        if (BuildConfig.FLAVOR == "mock") {
            okHttpClient
                .addInterceptor(mockInterceptor)
        }
        return okHttpClient.build()
    }

    @Provides
    fun providesJsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create(
        GsonBuilder()
            .setLenient()
            .create()
    )

    @Provides
    fun providesRetrofitClient(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
        encryptedPreferenceStorage: EncryptedPreferenceStorage
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(encryptedPreferenceStorage.baseUrl.toString())
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()

    @MobileConfigRetrofit
    @Provides
    fun providesMobileConfigRetrofitClient(
        @ApplicationContext context: Context,
        @MobileConfigOkHttp okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(context.getString(R.string.base_url))
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()

    @Provides
    fun providesHealthGateWayPrivateApi(retrofit: Retrofit): HealthGatewayPrivateApi =
        retrofit.create(HealthGatewayPrivateApi::class.java)

    @Provides
    fun providesHealthGateWayPublicApi(retrofit: Retrofit): HealthGatewayPublicApi =
        retrofit.create(HealthGatewayPublicApi::class.java)

    @Provides
    fun providesHealthGateWayMobileConfigApi(@MobileConfigRetrofit retrofit: Retrofit): HealthGatewayMobileConfigApi =
        retrofit.create(HealthGatewayMobileConfigApi::class.java)
}
