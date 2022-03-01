package ca.bc.gov.data.di

import android.content.Context
import androidx.room.Room
import ca.bc.gov.data.datasource.local.MyHealthDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

/**
 * @author Pinakin Kansara
 */
@Module
@InstallIn(SingletonComponent::class)
class TestMyHealthDataBaseModule {

    @Provides
    @Named("test_db")
    fun provideMyHealthDatabase(@ApplicationContext context: Context): MyHealthDataBase =
        Room.inMemoryDatabaseBuilder(
            context,
            MyHealthDataBase::class.java
        ).fallbackToDestructiveMigration().build()
}
