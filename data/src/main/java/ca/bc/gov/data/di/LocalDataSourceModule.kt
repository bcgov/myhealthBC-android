package ca.bc.gov.data.di

import ca.bc.gov.data.datasource.local.AppFeatureLocalDataSource
import ca.bc.gov.data.datasource.local.ClinicalDocumentLocalDataSource
import ca.bc.gov.data.datasource.local.CommentLocalDataSource
import ca.bc.gov.data.datasource.local.CovidOrderLocalDataSource
import ca.bc.gov.data.datasource.local.CovidTestLocalDataSource
import ca.bc.gov.data.datasource.local.DependentsLocalDataSource
import ca.bc.gov.data.datasource.local.DiagnosticImagingLocalDataSource
import ca.bc.gov.data.datasource.local.HealthVisitsLocalDataSource
import ca.bc.gov.data.datasource.local.HospitalVisitLocalDataSource
import ca.bc.gov.data.datasource.local.ImmunizationForecastLocalDataSource
import ca.bc.gov.data.datasource.local.ImmunizationRecommendationLocalDataSource
import ca.bc.gov.data.datasource.local.ImmunizationRecordLocalDataSource
import ca.bc.gov.data.datasource.local.LabOrderLocalDataSource
import ca.bc.gov.data.datasource.local.LabTestLocalDataSource
import ca.bc.gov.data.datasource.local.LocalDataSource
import ca.bc.gov.data.datasource.local.MedicationRecordLocalDataSource
import ca.bc.gov.data.datasource.local.MyHealthDataBase
import ca.bc.gov.data.datasource.local.NotificationLocalDataSource
import ca.bc.gov.data.datasource.local.OrganDonorLocalDataSource
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.data.datasource.local.QuickActionTileLocalDataSource
import ca.bc.gov.data.datasource.local.SpecialAuthorityLocalDataSource
import ca.bc.gov.data.datasource.local.UserProfileLocalDataSource
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
    fun provideDependentsLocalDataSource(
        db: MyHealthDataBase
    ): DependentsLocalDataSource = DependentsLocalDataSource(
        db.getDependentDao(),
        db.getPatientDao(),
        db.getDependentListOrderDao()
    )

    @Provides
    @Singleton
    fun provideImmunizationRecordLocalDataSource(db: MyHealthDataBase): ImmunizationRecordLocalDataSource =
        ImmunizationRecordLocalDataSource(db.getImmunizationRecordDao())

    @Provides
    @Singleton
    fun provideImmunizationRecommendationLocalDataSource(db: MyHealthDataBase): ImmunizationRecommendationLocalDataSource =
        ImmunizationRecommendationLocalDataSource(db.getImmunizationRecommendationDao())

    @Provides
    @Singleton
    fun provideImmunizationForecastLocalDataSource(db: MyHealthDataBase): ImmunizationForecastLocalDataSource =
        ImmunizationForecastLocalDataSource(db.getImmunizationForecastDao())

    @Provides
    @Singleton
    fun providesLocalDataSource(db: MyHealthDataBase) = LocalDataSource(db)

    @Provides
    @Singleton
    fun provideHealthVisitsLocalDataSource(db: MyHealthDataBase): HealthVisitsLocalDataSource =
        HealthVisitsLocalDataSource(db.getHealthVisitDao())

    @Provides
    @Singleton
    fun provideHospitalVisitLocalDataSource(db: MyHealthDataBase): HospitalVisitLocalDataSource =
        HospitalVisitLocalDataSource(db.getHospitalVisitDao())

    @Provides
    @Singleton
    fun provideNotificationLocalDataSource(db: MyHealthDataBase): NotificationLocalDataSource =
        NotificationLocalDataSource(db.getNotificationDao())

    @Provides
    @Singleton
    fun provideClinicalDocumentLocalDataSource(db: MyHealthDataBase): ClinicalDocumentLocalDataSource =
        ClinicalDocumentLocalDataSource(db.getClinicalDocumentDao())

    @Provides
    @Singleton
    fun provideSpecialAuthorityLocalDataSource(db: MyHealthDataBase): SpecialAuthorityLocalDataSource =
        SpecialAuthorityLocalDataSource(db.getSpecialAuthorityDao())

    @Provides
    @Singleton
    fun provideUserProfileLocalDataSource(db: MyHealthDataBase): UserProfileLocalDataSource =
        UserProfileLocalDataSource(db.getUserProfileDao())

    @Provides
    @Singleton
    fun providesOrganDonorLocalDataSource(db: MyHealthDataBase): OrganDonorLocalDataSource =
        OrganDonorLocalDataSource(db.getOrganDonationDao())

    @Provides
    @Singleton
    fun providesDiagnosticImagingLocalDataSource(db: MyHealthDataBase): DiagnosticImagingLocalDataSource =
        DiagnosticImagingLocalDataSource(db.getDiagnosticImagingDataDao())

    @Provides
    @Singleton
    fun provideAppFeatureLocalDataSource(db: MyHealthDataBase): AppFeatureLocalDataSource =
        AppFeatureLocalDataSource(db.getAppFeatureDao())

    @Provides
    @Singleton
    fun provideQuickAccessLocalDataSource(db: MyHealthDataBase): QuickActionTileLocalDataSource =
        QuickActionTileLocalDataSource(db.getQuickAccessTileDao())
}
