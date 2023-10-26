package ca.bc.gov.bchealth.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import ca.bc.gov.bchealth.MainActivity
import ca.bc.gov.bchealth.workers.WorkerInvoker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author: Created by Rashmi Bambhania on 25,February,2022
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesLaunchingIntent(@ApplicationContext context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    @Provides
    @Singleton
    fun provideWorkerInvoker(@ApplicationContext context: Context): WorkerInvoker =
        WorkerInvoker(context)

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)
}
