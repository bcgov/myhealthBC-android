package ca.bc.gov.repository.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import ca.bc.gov.common.BuildConfig.LOCAL_API_VERSION
import ca.bc.gov.common.R
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.common.model.immunization.ImmunizationDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
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
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.healthvisits.HealthVisitsRepository
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
    private val specialAuthorityRepository: SpecialAuthorityRepository,
    private val recordsRepository: RecordsRepository
) : CoroutineWorker(context, workerParams) {

    var isApiFailed = false

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

        isApiFailed = false
        val vaccineRecords = mutableListOf<PatientVaccineRecordsState?>()
        var covidOrderResponse: List<CovidOrderWithCovidTestDto>? = null
        var medicationResponse: MedicationStatementResponse? = null
        var labOrdersResponse: List<LabOrderWithLabTestDto>? = null
        var immunizationDto: ImmunizationDto? = null
        var commentsResponse: List<CommentDto>? = null
        var dependentsList: List<DependentDto>? = null
        var healthVisitsResponse: List<HealthVisitsDto>? = null
        var specialAuthorityResponse: List<SpecialAuthorityDto>? = null

        try {

            val authParameters = bcscAuthRepo.getAuthParametersDto()
            var patient: PatientDto? = null
            var patientId = 0L

            try {
                val isHgServicesUp = mobileConfigRepository.refreshMobileConfiguration()
                if (!isHgServicesUp) {
                    return respondToHgServicesDown()
                }
            } catch (e: Exception) {
                return respondToHgServicesDown()
            }

            notificationHelper.showNotification(
                context.getString(R.string.notification_title_while_fetching_data)
            )

            try {
                patient = patientWithBCSCLoginRepository.getPatient(
                    authParameters.token, authParameters.hdid
                )
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            // Insert patient details
            patient?.let {
                patientId = patientRepository.insertAuthenticatedPatient(it)
            }

            try {
                dependentsList = fetchDependents(authParameters)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            // Insert dependents
            dependentsList?.let { dependentsRepository.storeDependents(it, guardianId = patientId) }

            try {
                val patientVaccineRecords = fetchVaccineRecords(
                    authParameters.token,
                    authParameters.hdid,
                    patientId
                )
                vaccineRecords.add(patientVaccineRecords)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            val dependentVaccineRecords = fetchDependentsVaccineRecords(
                authParameters.token, dependentsList
            )
            vaccineRecords.addAll(dependentVaccineRecords)

            try {
                covidOrderResponse = fetchCovidTestResults(authParameters)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                medicationResponse = fetchMedicationResponse(authParameters)
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is MustBeQueuedException ->
                        if (e.message.toString().isNotBlank()) {
                            return handleQueueItException(e)
                        }
                    is ProtectiveWordException -> {
                        encryptedPreferenceStorage.protectiveWordState =
                            ProtectiveWordState.PROTECTIVE_WORD_REQUIRED.value
                    }
                    else -> {
                        isApiFailed = true
                    }
                }
            }

            try {
                labOrdersResponse = fetchLabTestResults(authParameters)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                immunizationDto = fetchImmunizations(authParameters)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                commentsResponse = fetchComments(authParameters)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                healthVisitsResponse = fetchHealthVisits(authParameters)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                specialAuthorityResponse = fetchSpecialAuthority(authParameters)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            /**
             * DB Operations
             * */
            // Insert vaccine records
            recordsRepository.storeVaccineRecords(vaccineRecords)

            // Insert covid orders
            recordsRepository.storeCovidOrders(patientId, covidOrderResponse)

            // Insert medication records
            medicationResponse?.let {
                medicationRecordRepository.updateMedicationRecords(it, patientId)
            }
            // Insert lab orders
            recordsRepository.storeLabOrders(patientId, labOrdersResponse)

            // Insert immunization records
            recordsRepository.storeImmunizationRecords(patientId, immunizationDto)

            // Insert comments
            commentsRepository.delete(true)
            commentsResponse?.let { commentsRepository.insert(it) }

            // Insert Health Visits
            healthVisitsRepository.deleteHealthVisits(patientId)
            healthVisitsResponse?.forEach {
                it.patientId = patientId
            }
            healthVisitsResponse?.let { healthVisitsRepository.insert(it) }

            // Insert Special authority
            specialAuthorityRepository.deleteSpecialAuthorities(patientId)
            specialAuthorityResponse?.forEach {
                it.patientId = patientId
            }
            specialAuthorityResponse?.let {
                specialAuthorityRepository.insert(it)
            }

            updateNotification(isApiFailed)
        } catch (e: Exception) {
            // no implementation required.
            e.printStackTrace()
        }
        return Result.success()
    }

    private fun updateNotification(isApiFailed: Boolean) {
        val notificationText = if (isApiFailed) {
            R.string.notification_title_on_failed
        } else {
            R.string.notification_title_on_success
        }
        notificationHelper.updateNotification(context.getString(notificationText))
    }

    private fun handleException(exception: Exception): Result? {
        Log.e("RecordsWorker", "Handling Exception:")
        exception.printStackTrace()
        return if (isQueueException(exception)) {
            handleQueueItException(exception)
        } else {
            isApiFailed = true
            null
        }
    }

    private fun isQueueException(exception: Exception) =
        exception is MustBeQueuedException && exception.message.toString().isNotBlank()

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

    /*
    * Fetch comments
    * */
    private suspend fun fetchComments(authParameters: AuthParametersDto): List<CommentDto>? {
        var commentsResponse: List<CommentDto>?
        withContext(dispatcher) {
            commentsResponse = commentsRepository.getComments(
                token = authParameters.token,
                hdid = authParameters.hdid
            )
        }
        return commentsResponse
    }

    private suspend fun fetchDependents(authParameters: AuthParametersDto): List<DependentDto>? {
        var dependents: List<DependentDto>?
        withContext(dispatcher) {
            dependents = dependentsRepository.fetchAllDependents(
                token = authParameters.token, hdid = authParameters.hdid
            )
        }
        return dependents
    }

    /*
     * Fetch immunizations
     * */
    private suspend fun fetchImmunizations(authParameters: AuthParametersDto): ImmunizationDto? {
        var immunizationDto: ImmunizationDto?
        withContext(dispatcher) {
            immunizationDto =
                immunizationRecordRepository.fetchImmunization(
                    token = authParameters.token,
                    hdid = authParameters.hdid
                )
        }
        return immunizationDto
    }

    /*
     * Fetch lab test results
     * */
    private suspend fun fetchLabTestResults(authParameters: AuthParametersDto): List<LabOrderWithLabTestDto>? {
        var labOrdersResponse: List<LabOrderWithLabTestDto>?
        withContext(dispatcher) {
            labOrdersResponse =
                labOrderRepository.fetchLabOrders(
                    token = authParameters.token,
                    hdid = authParameters.hdid
                )
        }
        return labOrdersResponse
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

    /*
    * Fetch covid test results
    * */
    private suspend fun fetchCovidTestResults(authParameters: AuthParametersDto): List<CovidOrderWithCovidTestDto>? {
        var covidOrderResponse: List<CovidOrderWithCovidTestDto>?
        withContext(dispatcher) {
            covidOrderResponse = covidOrderRepository.fetchCovidOrders(
                token = authParameters.token,
                hdid = authParameters.hdid
            )
        }
        return covidOrderResponse
    }

    /**
     * Fetch health visits
     */
    private suspend fun fetchHealthVisits(authParameters: AuthParametersDto): List<HealthVisitsDto>? {
        var healthVisitsResponse: List<HealthVisitsDto>?
        withContext(dispatcher) {
            healthVisitsResponse =
                healthVisitsRepository.getHealthVisits(
                    token = authParameters.token,
                    hdid = authParameters.hdid
                )
        }
        return healthVisitsResponse
    }

    /**
     * Fetch special authority
     */
    private suspend fun fetchSpecialAuthority(authParameters: AuthParametersDto): List<SpecialAuthorityDto>? {
        var specialAuthorityResponse: List<SpecialAuthorityDto>?
        withContext(dispatcher) {
            specialAuthorityResponse =
                specialAuthorityRepository.getSpecialAuthority(
                    token = authParameters.token,
                    hdid = authParameters.hdid
                )
        }
        return specialAuthorityResponse
    }

    private fun handleQueueItException(e: java.lang.Exception): Result {
        return Result.failure(
            Data.Builder()
                .putString(QUEUE_IT_URL, e.message.toString())
                .build()
        )
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

    companion object {
        const val APP_UPDATE_REQUIRED = "appUpdateRequired"
        const val IS_HG_SERVICES_UP = "isHgServicesUp"
        const val QUEUE_IT_URL = "queueItUrl"
    }
}
