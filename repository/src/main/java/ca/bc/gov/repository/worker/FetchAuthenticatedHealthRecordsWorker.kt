package ca.bc.gov.repository.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import ca.bc.gov.common.BuildConfig.FLAG_ADD_COMMENTS
import ca.bc.gov.common.BuildConfig.FLAG_HOSPITAL_VISITS
import ca.bc.gov.common.BuildConfig.LOCAL_API_VERSION
import ca.bc.gov.common.R
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
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
    private val recordsRepository: RecordsRepository
) : CoroutineWorker(context, workerParams) {

    private var isApiFailed = false

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
        var covidOrders: List<CovidOrderWithCovidTestDto>? = null
        var medications: MedicationStatementResponse? = null
        var labOrders: List<LabOrderWithLabTestDto>? = null
        var immunizations: ImmunizationDto? = null
        var comments: List<CommentDto>? = null
        var dependents: List<DependentDto>? = null
        var healthVisits: List<HealthVisitsDto>? = null
        var hospitalVisits: List<HospitalVisitDto>? = null
        var specialAuthorities: List<SpecialAuthorityDto>? = null

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
                dependents = fetchRecord(authParameters, dependentsRepository::fetchAllDependents)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            // Insert dependents
            dependents?.let { dependentsRepository.storeDependents(it, guardianId = patientId) }

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
                authParameters.token, dependents
            )
            vaccineRecords.addAll(dependentVaccineRecords)

            try {
                covidOrders = fetchRecord(authParameters, covidOrderRepository::fetchCovidOrders)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                medications = fetchMedicationResponse(authParameters)
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
                labOrders = fetchRecord(authParameters, labOrderRepository::fetchLabOrders)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                immunizations =
                    fetchRecord(authParameters, immunizationRecordRepository::fetchImmunization)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                if (FLAG_ADD_COMMENTS) {
                    comments = fetchRecord(authParameters, commentsRepository::getComments)
                }
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                healthVisits = fetchRecord(authParameters, healthVisitsRepository::getHealthVisits)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                if (FLAG_HOSPITAL_VISITS) {
                    hospitalVisits =
                        fetchRecord(authParameters, hospitalVisitRepository::getHospitalVisits)
                }
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                specialAuthorities =
                    fetchRecord(authParameters, specialAuthorityRepository::getSpecialAuthority)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            /**
             * DB Operations
             * */
            insertVaccineRecords(vaccineRecords)
            insertCovidOrders(patientId, covidOrders)
            insertMedicationRecords(patientId, medications)
            insertLabOrders(patientId, labOrders)
            insertImmunizationRecords(patientId, immunizations)
            insertComments(comments)
            insertHealthVisits(patientId, healthVisits)
            insertSpecialAuthority(patientId, specialAuthorities)
            insertHospitalVisits(patientId, hospitalVisits)

            updateNotification(isApiFailed)
        } catch (e: Exception) {
            // no implementation required.
            e.printStackTrace()
        }
        return Result.success()
    }

    private suspend fun insertSpecialAuthority(
        patientId: Long, specialAuthorities: List<SpecialAuthorityDto>?
    ) {
        specialAuthorityRepository.deleteSpecialAuthorities(patientId)
        specialAuthorities?.let { list ->
            list.forEach {
                it.patientId = patientId
            }
            specialAuthorityRepository.insert(list)
        }
    }

    private suspend fun insertHospitalVisits(
        patientId: Long, hospitalVisits: List<HospitalVisitDto>?
    ) {
        hospitalVisitRepository.deleteHospitalVisitsDto(patientId)
        hospitalVisits?.let { list ->
            list.forEach { it.patientId = patientId }
            hospitalVisitRepository.insertHospitalVisits(list)
        }
    }

    private suspend fun insertHealthVisits(
        patientId: Long, healthVisits: List<HealthVisitsDto>?
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

    private suspend fun insertImmunizationRecords(
        patientId: Long, immunizations: ImmunizationDto?
    ) {
        recordsRepository.storeImmunizationRecords(patientId, immunizations)
    }

    private suspend fun insertLabOrders(patientId: Long, labOrders: List<LabOrderWithLabTestDto>?) {
        recordsRepository.storeLabOrders(patientId, labOrders)
    }

    private suspend fun insertMedicationRecords(
        patientId: Long, medications: MedicationStatementResponse?
    ) {
        medications?.let {
            medicationRecordRepository.updateMedicationRecords(it, patientId)
        }
    }

    private suspend fun insertCovidOrders(
        patientId: Long, covidOrders: List<CovidOrderWithCovidTestDto>?
    ) {
        recordsRepository.storeCovidOrders(patientId, covidOrders)
    }

    private suspend fun insertVaccineRecords(vaccineRecords: MutableList<PatientVaccineRecordsState?>) {
        recordsRepository.storeVaccineRecords(vaccineRecords)
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
