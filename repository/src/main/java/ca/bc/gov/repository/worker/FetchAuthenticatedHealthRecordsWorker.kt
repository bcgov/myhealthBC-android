package ca.bc.gov.repository.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import ca.bc.gov.common.BuildConfig.FLAG_ADD_COMMENTS
import ca.bc.gov.common.BuildConfig.FLAG_CLINICAL_DOCUMENTS
import ca.bc.gov.common.BuildConfig.FLAG_HOSPITAL_VISITS
import ca.bc.gov.common.BuildConfig.LOCAL_API_VERSION
import ca.bc.gov.common.R
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.datasource.remote.model.response.MedicationStatementResponse
import ca.bc.gov.repository.CommentRepository
import ca.bc.gov.repository.DependentsRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.RecordsRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.bcsc.PostLoginCheck
import ca.bc.gov.repository.clinicaldocument.ClinicalDocumentRepository
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.healthvisits.HealthVisitsRepository
import ca.bc.gov.repository.hospitalvisit.HospitalVisitRepository
import ca.bc.gov.repository.immunization.ImmunizationRecordRepository
import ca.bc.gov.repository.labtest.LabOrderRepository
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.model.PatientVaccineRecordsState
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.qr.VaccineRecordState
import ca.bc.gov.repository.specialauthority.SpecialAuthorityRepository
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import ca.bc.gov.repository.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/*
* Created by amit_metri on 16,February,2022
*/
@HiltWorker
class FetchAuthenticatedHealthRecordsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fetchVaccineRecordRepository: FetchVaccineRecordRepository,
    private val bcscAuthRepo: BcscAuthRepo,
    private val medicationRecordRepository: MedicationRecordRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val patientRepository: PatientRepository,
    private val dependentsRepository: DependentsRepository,
    private val notificationHelper: NotificationHelper,
    private val labOrderRepository: LabOrderRepository,
    private val covidOrderRepository: CovidOrderRepository,
    private val encryptedPreferenceStorage: EncryptedPreferenceStorage,
    private val patientWithBCSCLoginRepository: PatientWithBCSCLoginRepository,
    private val mobileConfigRepository: MobileConfigRepository,
    private val immunizationRecordRepository: ImmunizationRecordRepository,
    private val commentsRepository: CommentRepository,
    private val healthVisitsRepository: HealthVisitsRepository,
    private val hospitalVisitRepository: HospitalVisitRepository,
    private val specialAuthorityRepository: SpecialAuthorityRepository,
    private val clinicalDocumentRepository: ClinicalDocumentRepository,
    private val recordsRepository: RecordsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val remoteVersion = mobileConfigRepository.getRemoteApiVersion()
        if (LOCAL_API_VERSION < remoteVersion) {
            return respondToAppUpdateRequired()
        }

        if (bcscAuthRepo.getPostLoginCheck() == PostLoginCheck.IN_PROGRESS.name ||
            !bcscAuthRepo.checkSession()
        ) {
            return Result.failure()
        }
        return fetchAuthRecords()
    }

    private suspend fun fetchAuthRecords(): Result {
        val dependents: List<DependentDto>?
        val authParameters = bcscAuthRepo.getAuthParametersDto()
        val patientId: Long

        try {
            val isHgServicesUp = mobileConfigRepository.refreshMobileConfiguration()
            if (!isHgServicesUp) {
                return respondToHgServicesDown()
            }
        } catch (e: Exception) {
            return respondToHgServicesDown()
        }

        notificationHelper.showNotification(context.getString(R.string.notification_title_while_fetching_data))

        try {
            val patient = patientWithBCSCLoginRepository.getPatient(
                authParameters.token, authParameters.hdid
            )

            patientId = patientRepository.insertAuthenticatedPatient(patient)
        } catch (e: Exception) {
            e.printStackTrace()
            updateNotification(isApiFailed = true)
            return Result.failure()
        }

        try {
            dependents = fetchRecord(authParameters, dependentsRepository::fetchAllDependents)
            dependents?.let { dependentsRepository.storeDependents(it, guardianId = patientId) }
        } catch (e: Exception) {
            e.printStackTrace()
            updateNotification(isApiFailed = true)
            return Result.failure()
        }

        val isApiFailed = loadRecords(patientId, authParameters, dependents)

        updateNotification(isApiFailed)

        return if (isApiFailed) {
            Result.failure()
        } else {
            Result.success()
        }
    }

    private suspend fun loadRecords(
        patientId: Long,
        authParameters: AuthParametersDto,
        dependents: List<DependentDto>?
    ): Boolean {
        val isApiFailed: Boolean

        withContext(dispatcher) {
            val taskResults = listOf(
                loadVaccineRecordsAsync(patientId, authParameters, dependents),
                loadMedicationsAsync(patientId, authParameters),
                loadLabOrdersAsync(patientId, authParameters),
                loadCovidOrdersAsync(patientId, authParameters),
                loadImmunizationsAsync(patientId, authParameters),
                loadHealthVisitsAsync(patientId, authParameters),
                loadClinicalDocumentsAsync(patientId, authParameters),
                loadHospitalVisitsAsync(patientId, authParameters),
                loadCommentsAsync(authParameters),
                loadSpecialAuthoritiesAsync(patientId, authParameters),
            ).awaitAll()

            isApiFailed = taskResults.contains(Result.failure())
        }
        return isApiFailed
    }

    private fun CoroutineScope.loadVaccineRecordsAsync(
        patientId: Long,
        authParameters: AuthParametersDto,
        dependents: List<DependentDto>?
    ) = runTaskAsync {
        val vaccineRecords = mutableListOf<PatientVaccineRecordsState?>()

        val patientVaccineRecords = fetchVaccineRecords(
            authParameters.token,
            authParameters.hdid,
            patientId
        )
        vaccineRecords.add(patientVaccineRecords)

        val dependentVaccineRecords = fetchDependentsVaccineRecords(
            authParameters.token, dependents
        )

        vaccineRecords.addAll(dependentVaccineRecords)
        recordsRepository.storeVaccineRecords(vaccineRecords)
    }

    private fun CoroutineScope.loadMedicationsAsync(
        patientId: Long,
        authParameters: AuthParametersDto
    ) = this.async {
        try {
            val medications = fetchMedicationResponse(authParameters)
            insertMedicationRecords(patientId, medications)
            Result.success()
        } catch (e: Exception) {
            when (e) {
                is ProtectiveWordException -> {
                    encryptedPreferenceStorage.protectiveWordState =
                        ProtectiveWordState.PROTECTIVE_WORD_REQUIRED.value
                    Result.failure()
                }
                else -> {
                    e.printStackTrace()
                    Result.failure()
                }
            }
        }
    }

    private fun CoroutineScope.loadLabOrdersAsync(
        patientId: Long,
        authParameters: AuthParametersDto
    ) = runTaskAsync {
        val labOrders = fetchRecord(authParameters, labOrderRepository::fetchLabOrders)
        recordsRepository.storeLabOrders(patientId, labOrders)
    }

    private fun CoroutineScope.loadCovidOrdersAsync(
        patientId: Long,
        authParameters: AuthParametersDto
    ) = runTaskAsync {
        val covidOrders = fetchRecord(authParameters, covidOrderRepository::fetchCovidOrders)
        recordsRepository.storeCovidOrders(patientId, covidOrders)
    }

    private fun CoroutineScope.loadClinicalDocumentsAsync(
        patientId: Long,
        authParameters: AuthParametersDto
    ) = runTaskAsync {
        if (FLAG_CLINICAL_DOCUMENTS) {
            val clinicalDocuments: List<ClinicalDocumentDto>? = fetchRecord(
                authParameters, clinicalDocumentRepository::getClinicalDocuments
            )

            clinicalDocuments?.let {
                recordsRepository.storeClinicalDocuments(patientId, it)
            }
        }
    }

    private fun CoroutineScope.loadHospitalVisitsAsync(
        patientId: Long,
        authParameters: AuthParametersDto
    ) = runTaskAsync {
        if (FLAG_HOSPITAL_VISITS) {
            val hospitalVisits: List<HospitalVisitDto>? = fetchRecord(
                authParameters, hospitalVisitRepository::getHospitalVisits
            )

            hospitalVisits?.let {
                recordsRepository.storeHospitalVisits(patientId, it)
            }
        }
    }

    private fun CoroutineScope.loadHealthVisitsAsync(
        patientId: Long,
        authParameters: AuthParametersDto
    ) = runTaskAsync {
        val healthVisits: List<HealthVisitsDto>? = fetchRecord(
            authParameters, healthVisitsRepository::getHealthVisits
        )
        insertHealthVisits(patientId, healthVisits)
    }

    private fun CoroutineScope.loadCommentsAsync(
        authParameters: AuthParametersDto
    ) = runTaskAsync {
        if (FLAG_ADD_COMMENTS) {
            val comments: List<CommentDto>? = fetchRecord(
                authParameters, commentsRepository::getComments
            )

            insertComments(comments)
        }
    }

    private fun CoroutineScope.loadImmunizationsAsync(
        patientId: Long,
        authParameters: AuthParametersDto
    ) = runTaskAsync {
        val immunizations = fetchRecord(
            authParameters, immunizationRecordRepository::fetchImmunization
        )

        recordsRepository.storeImmunizationRecords(patientId, immunizations)
    }

    private fun CoroutineScope.loadSpecialAuthoritiesAsync(
        patientId: Long,
        authParameters: AuthParametersDto
    ) = runTaskAsync {
        val specialAuthorities = fetchRecord(
            authParameters, specialAuthorityRepository::getSpecialAuthority
        )

        insertSpecialAuthority(patientId, specialAuthorities)
    }

    private suspend fun insertSpecialAuthority(
        patientId: Long,
        specialAuthorities: List<SpecialAuthorityDto>?
    ) {
        specialAuthorityRepository.deleteSpecialAuthorities(patientId)
        specialAuthorities?.let { list ->
            list.forEach {
                it.patientId = patientId
            }
            specialAuthorityRepository.insert(list)
        }
    }

    private suspend fun insertHealthVisits(
        patientId: Long,
        healthVisits: List<HealthVisitsDto>?
    ) {
        healthVisitsRepository.deleteHealthVisits(patientId)
        healthVisits?.let { list ->
            list.forEach {
                it.patientId = patientId
            }
            healthVisitsRepository.insert(list)
        }
    }

    private suspend fun insertComments(comments: List<CommentDto>?) {
        commentsRepository.delete(true)
        comments?.let { commentsRepository.insert(it) }
    }

    private suspend fun insertMedicationRecords(
        patientId: Long,
        medications: MedicationStatementResponse?
    ) {
        medications?.let {
            medicationRecordRepository.updateMedicationRecords(it, patientId)
        }
    }

    private fun updateNotification(isApiFailed: Boolean) {
        val notificationText = if (isApiFailed) {
            R.string.notification_title_on_failed
        } else {
            R.string.notification_title_on_success
        }
        notificationHelper.updateNotification(context.getString(notificationText))
    }

    private suspend fun fetchDependentsVaccineRecords(
        token: String,
        dependents: List<DependentDto>?
    ): List<PatientVaccineRecordsState> {
        val resultList = mutableListOf<PatientVaccineRecordsState>()

        dependents?.forEach { dependent ->
            try {
                val patientId = dependentsRepository.getDependentByPhn(dependent.phn).patientId

                fetchVaccineRecords(
                    token,
                    dependent.hdid,
                    patientId
                )?.let { dependentVaccineRecord ->
                    resultList.add(dependentVaccineRecord)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return resultList
    }

    private suspend fun <T> fetchRecord(
        authParameters: AuthParametersDto,
        action: suspend (String, String) -> T?
    ): T? {
        var response: T?
        withContext(dispatcher) {
            response = action.invoke(authParameters.token, authParameters.hdid)
        }
        return response
    }

    /*
    * Fetch medication records
    * */
    private suspend fun fetchMedicationResponse(authParameters: AuthParametersDto): MedicationStatementResponse? {
        var medicationResponse: MedicationStatementResponse?
        withContext(dispatcher) {
            medicationResponse = medicationRecordRepository.fetchMedicationStatement(
                token = authParameters.token,
                hdid = authParameters.hdid,
                encryptedPreferenceStorage.protectiveWord
            )
        }
        return medicationResponse
    }

    /*
    * Fetch vaccine records
    * */
    private suspend fun fetchVaccineRecords(
        token: String,
        hdid: String,
        patientId: Long
    ): PatientVaccineRecordsState? {
        var response: Pair<VaccineRecordState, PatientVaccineRecord?>?
        withContext(dispatcher) {
            response = fetchVaccineRecordRepository.fetchVaccineRecord(token, hdid)
        }
        return response?.let {
            PatientVaccineRecordsState(
                patientId = patientId,
                vaccineRecordState = it.first,
                patientVaccineRecord = it.second
            )
        }
    }

    private fun respondToHgServicesDown(): Result {
        return Result.failure(
            Data.Builder()
                .putBoolean(IS_HG_SERVICES_UP, false)
                .build()
        )
    }

    private fun respondToAppUpdateRequired(): Result {
        return Result.failure(
            Data.Builder()
                .putBoolean(APP_UPDATE_REQUIRED, true)
                .build()
        )
    }

    private fun CoroutineScope.runTaskAsync(task: suspend () -> Unit): Deferred<Result> =
        this.async {
            try {
                task.invoke()
                Result.success()
            } catch (e: Exception) {
                Log.e("RecordsWorker", "Handling Exception:")
                e.printStackTrace()
                Result.failure()
            }
        }

    companion object {
        const val APP_UPDATE_REQUIRED = "appUpdateRequired"
        const val IS_HG_SERVICES_UP = "isHgServicesUp"

        const val QUEUE_IT_URL = "queueItUrl"
    }
}
