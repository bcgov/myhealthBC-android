package ca.bc.gov.data.di

import ca.bc.gov.data.datasource.local.CommentLocalDataSource
import ca.bc.gov.data.datasource.local.CovidOrderLocalDataSource
import ca.bc.gov.data.datasource.local.CovidTestLocalDataSource
import ca.bc.gov.data.datasource.local.ImmunizationForecastLocalDataSource
import ca.bc.gov.data.datasource.local.ImmunizationRecordLocalDataSource
import ca.bc.gov.data.datasource.local.LabOrderLocalDataSource
import ca.bc.gov.data.datasource.local.LabTestLocalDataSource
import ca.bc.gov.data.datasource.local.LocalDataSource
import ca.bc.gov.data.datasource.local.MedicationRecordLocalDataSource
import ca.bc.gov.data.datasource.local.MyHealthDataBase
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.data.datasource.local.TestResultLocalDataSource
import ca.bc.gov.data.datasource.local.VaccineRecordLocalDataSource
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
    fun providesLabOrderLocalDataSource(
        db: MyHealthDataBase
    ) = LabOrderLocalDataSource(db.getLabOrderDao())

    @Provides
    @Singleton
    fun providesLabTestLocalDataSource(
        db: MyHealthDataBase
    ) = LabTestLocalDataSource(db.getLabTestDao())

    @Provides
    @Singleton
    fun provideCommentLocalDataSource(
        db: MyHealthDataBase
    ) = CommentLocalDataSource(db.getCommentDao())

    @Provides
    @Singleton
    fun provideCovidOrderLocalDataSource(
        db: MyHealthDataBase
    ): CovidOrderLocalDataSource = CovidOrderLocalDataSource(db.getCovidOrderDao())

    @Provides
    @Singleton
    fun provideCovidTestLocalDataSource(
        db: MyHealthDataBase
    ): CovidTestLocalDataSource = CovidTestLocalDataSource(db.getCovidTestDao())

    @Provides
    @Singleton
    fun provideImmunizationRecordLocalDataSource(db: MyHealthDataBase): ImmunizationRecordLocalDataSource =
        ImmunizationRecordLocalDataSource(db.getImmunizationRecordDao())

    @Provides
    @Singleton
    fun provideImmunizationForecastLocalDataSource(db: MyHealthDataBase): ImmunizationForecastLocalDataSource =
        ImmunizationForecastLocalDataSource(db.getImmunizationForecastDao())

    @Provides
    @Singleton
    fun providesLocalDataSource(db: MyHealthDataBase) = LocalDataSource(db)
}
