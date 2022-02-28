package ca.bc.gov.data.di

import ca.bc.gov.data.datasource.local.LocalDataSource
import ca.bc.gov.data.datasource.local.MedicationRecordLocalDataSource
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.data.datasource.local.TestResultLocalDataSource
import ca.bc.gov.data.datasource.local.VaccineRecordLocalDataSource
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
    fun providesTestResultLocalDataSource(
        db: MyHealthDataBase
    ) = TestResultLocalDataSource(db.getTestResultDao())

    @Provides
    @Singleton
    fun providesMedicationRecordLocalDataSource(
        db: MyHealthDataBase
    ) = MedicationRecordLocalDataSource(
        db.getMedicationRecordDao(),
        db.getMedicationSummaryDao(),
        db.getDispensingPharmacyDao()
    )

    @Provides
    @Singleton
    fun providesLocalDataSource(db: MyHealthDataBase) = LocalDataSource(db)
}
