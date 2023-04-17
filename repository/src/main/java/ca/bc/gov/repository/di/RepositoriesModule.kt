package ca.bc.gov.repository.di

import android.content.Context
import ca.bc.gov.data.datasource.local.CommentLocalDataSource
import ca.bc.gov.data.datasource.local.CovidOrderLocalDataSource
import ca.bc.gov.data.datasource.local.CovidTestLocalDataSource
import ca.bc.gov.data.datasource.local.DependentsLocalDataSource
import ca.bc.gov.data.datasource.local.ImmunizationForecastLocalDataSource
import ca.bc.gov.data.datasource.local.ImmunizationRecordLocalDataSource
import ca.bc.gov.data.datasource.local.LabOrderLocalDataSource
import ca.bc.gov.data.datasource.local.LabTestLocalDataSource
import ca.bc.gov.data.datasource.local.LocalDataSource
import ca.bc.gov.data.datasource.local.MedicationRecordLocalDataSource
import ca.bc.gov.data.datasource.local.OrganDonationLocalDataSource
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.data.datasource.local.VaccineRecordLocalDataSource
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.datasource.remote.BannerRemoteDataSource
import ca.bc.gov.data.datasource.remote.CommentRemoteDataSource
import ca.bc.gov.data.datasource.remote.DependentsRemoteDataSource
import ca.bc.gov.data.datasource.remote.FeedbackRemoteDataSource
import ca.bc.gov.data.datasource.remote.ImmunizationRemoteDataSource
import ca.bc.gov.data.datasource.remote.LaboratoryRemoteDataSource
import ca.bc.gov.data.datasource.remote.MedicationRemoteDataSource
import ca.bc.gov.data.datasource.remote.OrganDonorRemoteDataSource
import ca.bc.gov.data.datasource.remote.TermsOfServiceRemoteDataSource
import ca.bc.gov.repository.BannerRepository
import ca.bc.gov.repository.ClearStorageRepository
import ca.bc.gov.repository.CommentRepository
import ca.bc.gov.repository.DependentsRepository
import ca.bc.gov.repository.FeedbackRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.OnBoardingRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.PdfDecoderRepository
import ca.bc.gov.repository.QrCodeGeneratorRepository
import ca.bc.gov.repository.RecentPhnDobRepository
import ca.bc.gov.repository.RecordsRepository
import ca.bc.gov.repository.TermsOfServiceRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.clinicaldocument.ClinicalDocumentRepository
import ca.bc.gov.repository.hospitalvisit.HospitalVisitRepository
import ca.bc.gov.repository.immunization.ImmunizationForecastRepository
import ca.bc.gov.repository.immunization.ImmunizationRecommendationRepository
import ca.bc.gov.repository.immunization.ImmunizationRecordRepository
import ca.bc.gov.repository.labtest.LabOrderRepository
import ca.bc.gov.repository.labtest.LabTestRepository
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.qr.ProcessQrRepository
import ca.bc.gov.repository.scanner.QrScanner
import ca.bc.gov.repository.services.OrganDonorRepository
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import ca.bc.gov.repository.testrecord.CovidTestRepository
import ca.bc.gov.repository.utils.Base64ToInputImageConverter
import ca.bc.gov.repository.utils.UriToImage
import ca.bc.gov.repository.vaccine.VaccineRecordRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import ca.bc.gov.shcdecoder.SHCVerifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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
    fun providesClearStorageRepository(
        localDataSource: LocalDataSource,
        preferenceStorage: EncryptedPreferenceStorage,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ) =
        ClearStorageRepository(localDataSource, preferenceStorage, ioDispatcher)

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
        PdfDecoderRepository(context)

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
        medicationRemoteDataSource: MedicationRemoteDataSource,
        encryptedPreferenceStorage: EncryptedPreferenceStorage
    ): MedicationRecordRepository = MedicationRecordRepository(
        medicationRecordLocalDataSource,
        medicationRemoteDataSource,
        encryptedPreferenceStorage
    )

    @Provides
    @Singleton
    fun provideLabOrderRepository(
        laboratoryRemoteDataSource: LaboratoryRemoteDataSource,
        labOrderLocalDataSource: LabOrderLocalDataSource,
        bcscAuthRepo: BcscAuthRepo
    ): LabOrderRepository = LabOrderRepository(
        laboratoryRemoteDataSource,
        labOrderLocalDataSource,
        bcscAuthRepo
    )

    @Provides
    @Singleton
    fun provideLabTestRepository(
        labTestLocalDataSource: LabTestLocalDataSource
    ): LabTestRepository = LabTestRepository(
        labTestLocalDataSource
    )

    @Provides
    @Singleton
    fun provideCommentRepository(
        commentLocalDataSource: CommentLocalDataSource,
        commentRemoteDataSource: CommentRemoteDataSource,
        bcscAuthRepo: BcscAuthRepo,
        @ApplicationContext context: Context
    ): CommentRepository =
        CommentRepository(commentRemoteDataSource, commentLocalDataSource, bcscAuthRepo, context)

    @Provides
    @Singleton
    fun provideRecordsRepository(
        patientWithVaccineRecordRepository: PatientWithVaccineRecordRepository,
        covidOrderRepository: CovidOrderRepository,
        covidTestRepository: CovidTestRepository,
        labOrderRepository: LabOrderRepository,
        labTestRepository: LabTestRepository,
        immunizationRecordRepository: ImmunizationRecordRepository,
        immunizationForecastRepository: ImmunizationForecastRepository,
        immunizationRecommendationRepository: ImmunizationRecommendationRepository,
        hospitalVisitRepository: HospitalVisitRepository,
        clinicalDocumentRepository: ClinicalDocumentRepository
    ): RecordsRepository = RecordsRepository(
        patientWithVaccineRecordRepository,
        covidOrderRepository,
        covidTestRepository,
        labOrderRepository,
        labTestRepository,
        immunizationRecordRepository,
        immunizationForecastRepository,
        immunizationRecommendationRepository,
        hospitalVisitRepository,
        clinicalDocumentRepository
    )

    @Provides
    @Singleton
    fun provideDependentsRepository(
        remoteDataSource: DependentsRemoteDataSource,
        localDataSource: DependentsLocalDataSource,
        patientLocalDataSource: PatientLocalDataSource,
        bcscAuthRepo: BcscAuthRepo,
        covidOrderRepository: CovidOrderRepository,
        fetchVaccineRecordRepository: FetchVaccineRecordRepository,
        immunizationRecordRepository: ImmunizationRecordRepository,
        labOrderRepository: LabOrderRepository,
        recordsRepository: RecordsRepository,
        clinicalDocumentRepository: ClinicalDocumentRepository,
        mobileConfigRepository: MobileConfigRepository
    ): DependentsRepository = DependentsRepository(
        remoteDataSource,
        localDataSource,
        patientLocalDataSource,
        bcscAuthRepo,
        covidOrderRepository,
        fetchVaccineRecordRepository,
        immunizationRecordRepository,
        labOrderRepository,
        recordsRepository,
        clinicalDocumentRepository,
        mobileConfigRepository
    )

    @Provides
    @Singleton
    fun provideTermsOfServiceRepository(
        termsOfServiceRemoteDataSource: TermsOfServiceRemoteDataSource
    ): TermsOfServiceRepository = TermsOfServiceRepository(termsOfServiceRemoteDataSource)

    @Provides
    @Singleton
    fun provideCovidOrderRepository(
        laboratoryRemoteDataSource: LaboratoryRemoteDataSource,
        covidOrderLocalDataSource: CovidOrderLocalDataSource,
        dependentsLocalDataSource: DependentsLocalDataSource,
        bcscAuthRepo: BcscAuthRepo
    ): CovidOrderRepository =
        CovidOrderRepository(
            laboratoryRemoteDataSource,
            covidOrderLocalDataSource,
            dependentsLocalDataSource,
            bcscAuthRepo
        )

    @Provides
    @Singleton
    fun provideCovidTestRepository(covidTestLocalDataSource: CovidTestLocalDataSource): CovidTestRepository =
        CovidTestRepository(covidTestLocalDataSource)

    @Provides
    @Singleton
    fun providesImmunizationRepository(
        immunizationRemoteDataSource: ImmunizationRemoteDataSource,
        immunizationRecordLocalDataSource: ImmunizationRecordLocalDataSource
    ): ImmunizationRecordRepository =
        ImmunizationRecordRepository(
            immunizationRemoteDataSource,
            immunizationRecordLocalDataSource
        )

    @Provides
    @Singleton
    fun providesImmunizationForecastRepository(
        immunizationForecastLocalDataSource: ImmunizationForecastLocalDataSource
    ): ImmunizationForecastRepository =
        ImmunizationForecastRepository(immunizationForecastLocalDataSource)

    @Provides
    @Singleton
    fun providesBannerRepository(
        remoteDataSource: BannerRemoteDataSource
    ): BannerRepository =
        BannerRepository(remoteDataSource)

    @Provides
    @Singleton
    fun providesFeedbackRepository(
        remoteDataSource: FeedbackRemoteDataSource,
        bcscAuthRepo: BcscAuthRepo,
    ): FeedbackRepository = FeedbackRepository(remoteDataSource, bcscAuthRepo)

    @Provides
    @Singleton
    fun providesOrganDonorRepository(
        remoteDataSource: OrganDonorRemoteDataSource,
        localDataSource: OrganDonationLocalDataSource
    ): OrganDonorRepository = OrganDonorRepository(remoteDataSource, localDataSource)
}
