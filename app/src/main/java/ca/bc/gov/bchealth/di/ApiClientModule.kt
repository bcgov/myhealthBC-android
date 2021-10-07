package ca.bc.gov.bchealth.di

import android.content.Context
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.services.ImmunizationServices
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

    @Singleton
    @Provides
    fun provideRetrofit(uniqueRetrofitConstant: String, context: Context):
            Retrofit {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()

        /*
        * For logging request and responses
        * */
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }

        val okHttpClient = builder.build()

        val gson = GsonBuilder()
            .setDateFormat(DateFormat.LONG)
            .create()

        /*
        * Create different retrofit objects based on uniqueRetrofit constant
        * An app might be using different API endpoints with different base URLs
        * So it is required to create different Retrofit objects based on the API endpoint requested
        *  */

        return when (uniqueRetrofitConstant) {
            SERVICE_IMMUNIZATION ->
                Retrofit.Builder()
                    .baseUrl(context.getString(R.string.retrofit_url_immunization))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build()
            // TODO: 06/10/21 Add more retrofit objects here
            else ->
                //Default is retrofit end point
                Retrofit.Builder()
                    .baseUrl(context.getString(R.string.retrofit_url_immunization))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build()
        }
    }

    @Singleton
    @Provides
    fun provideImmunizationServices(@ApplicationContext context: Context):
            ImmunizationServices {
        return provideRetrofit(SERVICE_IMMUNIZATION, context = context)
            .create(ImmunizationServices::class.java)
    }


    companion object {
        const val SERVICE_IMMUNIZATION = "SERVICE_IMMUNIZATION"
    }

}