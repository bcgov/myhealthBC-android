package ca.bc.gov.data.di

import android.content.Context
import androidx.room.Room
import ca.bc.gov.data.local.MyHealthDataBase
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
class MyHealthDataBaseModule {

    @Provides
    @Singleton
    fun providesMyHealthDataBase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        MyHealthDataBase::class.java,
        "my_health_db"
    ).build()
}
