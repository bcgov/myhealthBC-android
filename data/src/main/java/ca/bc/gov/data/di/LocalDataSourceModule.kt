package ca.bc.gov.data.di

import ca.bc.gov.data.datasource.LocalDataSource
import ca.bc.gov.data.datasource.PatientLocalDataSource
import ca.bc.gov.data.datasource.TestRecordLocalDataSource
import ca.bc.gov.data.datasource.TestResultLocalDataSource
import ca.bc.gov.data.datasource.VaccineDoseLocalDataSource
import ca.bc.gov.data.datasource.VaccineRecordLocalDataSource
import ca.bc.gov.data.local.MyHealthDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author Pinakin Kansara
 */
@Module
@InstallIn(SingletonComponent::class)
class LocalDataSourceModule {

    @Provides
    @Singleton
    fun providesPatientLocalDataSource(db: MyHealthDataBase) =
        PatientLocalDataSource(db.getPatientDao())

    @Provides
    @Singleton
    fun providesVaccineRecordLocalDataSource(db: MyHealthDataBase) =
        VaccineRecordLocalDataSource(db.getVaccineRecordDao())

    @Provides
    @Singleton
    fun providesVaccineDoseLocalDataSource(db: MyHealthDataBase) =
        VaccineDoseLocalDataSource(db.getVaccineDoseDao())

    @Provides
    @Singleton
    fun providesTestResultLocalDataSource(
        db: MyHealthDataBase
    ) = TestResultLocalDataSource(db.getTestResultDao())

    @Provides
    @Singleton
    fun providesTestRecordLocalDataSource(
        db: MyHealthDataBase
    ) = TestRecordLocalDataSource(db.getTestRecordDao())

    @Provides
    @Singleton
    fun providesLocalDataSource(db: MyHealthDataBase) = LocalDataSource(db)
}
