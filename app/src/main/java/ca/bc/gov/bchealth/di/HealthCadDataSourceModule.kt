package ca.bc.gov.bchealth.di

import android.content.Context
import androidx.room.Room
import ca.bc.gov.bchealth.data.local.BcVaccineCardDataBase
import ca.bc.gov.bchealth.datasource.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * [HealthCadDataSourceModule]
 *
 * @author Pinakin Kansara
 */
@Module
@InstallIn(SingletonComponent::class)
class HealthCadDataSourceModule {

    @Provides
    @Singleton
    fun providesDataBase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        BcVaccineCardDataBase::class.java,
        "bc_vaccine_card_db"
    ).build()

    @Provides
    @Singleton
    fun provideLocalDataSource(dataBase: BcVaccineCardDataBase) = LocalDataSource(dataBase)
}
