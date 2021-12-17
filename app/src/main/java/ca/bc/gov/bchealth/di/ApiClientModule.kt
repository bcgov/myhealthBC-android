package ca.bc.gov.bchealth.di

import android.content.Context
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.http.AddCookiesInterceptor
import ca.bc.gov.bchealth.http.CookieStorage
import ca.bc.gov.bchealth.http.QueueITInterceptor
import ca.bc.gov.bchealth.http.ReceivedCookiesInterceptor
import ca.bc.gov.bchealth.http.UserAgentInterceptor
import ca.bc.gov.bchealth.services.ImmunizationServices
import ca.bc.gov.bchealth.services.LaboratoryServices
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
import java.text.DateFormat
import javax.inject.Singleton

/**
 * [ApiClientModule]
 *
 * @author amit metri
 */
@Module
@InstallIn(SingletonComponent::class)
class ApiClientModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        context: Context
    ): Retrofit {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()

        /*
        * For logging request and responses
        * */
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        /*
        * Queue.it
        * */
        val cookies = CookieStorage()
        builder.addInterceptor(QueueITInterceptor(cookies))
            .addNetworkInterceptor(AddCookiesInterceptor(cookies))
            .addNetworkInterceptor(ReceivedCookiesInterceptor(cookies))
            .addInterceptor(
                UserAgentInterceptor(
                    context.getString(R.string.app_name),
                    BuildConfig.VERSION_NAME
                )
            )
            .addInterceptor(interceptor = interceptor)
            .hostnameVerifier { _, _ -> true }

        val okHttpClient = builder.build()

        val gson = GsonBuilder()
            .setDateFormat(DateFormat.LONG)
            .create()

        /*
        * To create different retrofit objects use uniqueRetrofit constant.
        * An app might be using different API endpoints with different base URLs
        * So it is required to create different Retrofit objects based on the API endpoint requested
        * */
        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.retrofit_url_immunization))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideImmunizationServices(@ApplicationContext context: Context): ImmunizationServices {
        return provideRetrofit(
            context = context
        ).create(ImmunizationServices::class.java)
    }

    @Provides
    @Singleton
    fun provideLaboratoryServices(@ApplicationContext context: Context): LaboratoryServices {
        return provideRetrofit(
            context = context
        ).create(LaboratoryServices::class.java)
    }

    companion object {
        var queueItToken: String = ""
    }
}
