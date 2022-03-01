package ca.bc.gov.repository.di

import android.content.Context
import ca.bc.gov.data.datasource.local.LabOrderLocalDataSource
import ca.bc.gov.data.datasource.local.LabTestLocalDataSource
import ca.bc.gov.data.datasource.local.LocalDataSource
import ca.bc.gov.data.datasource.local.MedicationRecordLocalDataSource
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.data.datasource.local.TestResultLocalDataSource
import ca.bc.gov.data.datasource.local.VaccineRecordLocalDataSource
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.datasource.remote.ImmunizationRemoteDataSource
import ca.bc.gov.data.datasource.remote.LaboratoryRemoteDataSource
import ca.bc.gov.data.datasource.remote.MedicationRemoteDataSource
import ca.bc.gov.repository.ClearStorageRepository
import ca.bc.gov.repository.FederalTravelPassDecoderRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.OnBoardingRepository
import ca.bc.gov.repository.PatientWithTestResultRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.QrCodeGeneratorRepository
import ca.bc.gov.repository.QueueItTokenRepository
import ca.bc.gov.repository.RecentPhnDobRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.labtest.LabOrderRepository
import ca.bc.gov.repository.labtest.LabTestRepository
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.qr.ProcessQrRepository
import ca.bc.gov.repository.scanner.QrScanner
import ca.bc.gov.repository.testrecord.TestResultRepository
import ca.bc.gov.repository.utils.Base64ToInputImageConverter
import ca.bc.gov.repository.utils.UriToImage
import ca.bc.gov.repository.vaccine.VaccineRecordRepository
import ca.bc.gov.shcdecoder.SHCVerifier
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
class RepositoriesModule {

    @Provides
    fun providesBase64ToImageConverter() = Base64ToInputImageConverter()

    @Provides
    @Singleton
    fun providesQueueItTokenRepository(
        preferenceStorage: EncryptedPreferenceStorage
    ) = QueueItTokenRepository(preferenceStorage)

    @Provides
    @Singleton
    fun provideProcessQrRepository(
        qrScanner: QrScanner,
        uriToImage: UriToImage,
        shcVerifier: SHCVerifier,
        patientRepository: PatientRepository
    ) = ProcessQrRepository(
        qrScanner, uriToImage, shcVerifier, patientRepository
    )

    @Provides
    @Singleton
    fun providesFetchVaccineRecordRepository(
        base64ToInputImageConverter: Base64ToInputImageConverter,
        immunizationRemoteDataSource: ImmunizationRemoteDataSource,
        processQrRepository: ProcessQrRepository
    ) = FetchVaccineRecordRepository(
        base64ToInputImageConverter, immunizationRemoteDataSource, processQrRepository
    )

    @Provides
    @Singleton
    fun providesPatientRepository(
        localDataSource: PatientLocalDataSource,
        qrCodeGeneratorRepository: QrCodeGeneratorRepository
    ) =
        PatientRepository(localDataSource, qrCodeGeneratorRepository)

    @Provides
    @Singleton
    fun providesVaccineRecordRepository(localDataSource: VaccineRecordLocalDataSource) =
        VaccineRecordRepository(localDataSource)

    @Provides
    @Singleton
    fun providesTestResultRepository(localDataSource: TestResultLocalDataSource) =
        TestResultRepository(localDataSource)

    @Provides
    @Singleton
    fun providesQrCodeGeneratorRepository() = QrCodeGeneratorRepository()

    @Provides
    @Singleton
    fun providesPatientWithVaccineRepository(
        patientRepository: PatientRepository,
        vaccineRecordRepository: VaccineRecordRepository
    ) = PatientWithVaccineRecordRepository(
        patientRepository,
        vaccineRecordRepository
    )

    @Provides
    @Singleton
    fun providesPatientWithTestResultRepository(
        patientRepository: PatientRepository,
        testResultRepository: TestResultRepository
    ) = PatientWithTestResultRepository(
        patientRepository,
        testResultRepository
    )

    @Provides
    @Singleton
    fun providesClearStorageRepository(
        localDataSource: LocalDataSource,
        preferenceStorage: EncryptedPreferenceStorage
    ) =
        ClearStorageRepository(localDataSource, preferenceStorage)

    @Provides
    @Singleton
    fun providesOnBoardingRepository(preferenceStorage: EncryptedPreferenceStorage) =
        OnBoardingRepository(preferenceStorage)

    @Provides
    @Singleton
    fun provideRecentPhnDobRepository(preferenceStorage: EncryptedPreferenceStorage) =
        RecentPhnDobRepository(preferenceStorage)

    @Provides
    @Singleton
    fun provideFetchTravelPassDecoderRepository(@ApplicationContext context: Context) =
        FederalTravelPassDecoderRepository(context)

    @Provides
    @Singleton
    fun provideBcscAuthRepository(
        @ApplicationContext context: Context,
        encryptedPreferenceStorage: EncryptedPreferenceStorage,
        patientLocalDataSource: PatientLocalDataSource
    ) = BcscAuthRepo(
        context,
        encryptedPreferenceStorage,
        patientLocalDataSource
    )

    @Provides
    @Singleton
    fun provideMedicationRecordRepository(
        medicationRecordLocalDataSource: MedicationRecordLocalDataSource,
        medicationRemoteDataSource: MedicationRemoteDataSource
    ): MedicationRecordRepository = MedicationRecordRepository(
        medicationRecordLocalDataSource,
        medicationRemoteDataSource
    )

    @Provides
    @Singleton
    fun provideLabOrderRepository(
        laboratoryRemoteDataSource: LaboratoryRemoteDataSource,
        labOrderLocalDataSource: LabOrderLocalDataSource
    ): LabOrderRepository = LabOrderRepository(
        laboratoryRemoteDataSource,
        labOrderLocalDataSource
    )

    @Provides
    @Singleton
    fun provideLabTestRepository(
        labTestLocalDataSource: LabTestLocalDataSource
    ): LabTestRepository = LabTestRepository(
        labTestLocalDataSource
    )
}
