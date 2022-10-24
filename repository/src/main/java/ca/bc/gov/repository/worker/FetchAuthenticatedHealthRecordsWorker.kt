package ca.bc.gov.repository.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import ca.bc.gov.common.R
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.ProtectiveWordException
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
import ca.bc.gov.repository.PatientWithTestResultRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.bcsc.PostLoginCheck
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.healthvisits.HealthVisitsRepository
import ca.bc.gov.repository.immunization.ImmunizationForecastRepository
import ca.bc.gov.repository.immunization.ImmunizationRecommendationRepository
import ca.bc.gov.repository.immunization.ImmunizationRecordRepository
import ca.bc.gov.repository.labtest.LabOrderRepository
import ca.bc.gov.repository.labtest.LabTestRepository
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.qr.VaccineRecordState
import ca.bc.gov.repository.specialauthority.SpecialAuthorityRepository
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import ca.bc.gov.repository.testrecord.CovidTestRepository
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
    private val patientWithVaccineRecordRepository: PatientWithVaccineRecordRepository,
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    private val medicationRecordRepository: MedicationRecordRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val patientRepository: PatientRepository,
    private val dependentsRepository: DependentsRepository,
    private val notificationHelper: NotificationHelper,
    private val labOrderRepository: LabOrderRepository,
    private val labTestRepository: LabTestRepository,
    private val covidOrderRepository: CovidOrderRepository,
    private val covidTestRepository: CovidTestRepository,
    private val encryptedPreferenceStorage: EncryptedPreferenceStorage,
    private val patientWithBCSCLoginRepository: PatientWithBCSCLoginRepository,
    private val mobileConfigRepository: MobileConfigRepository,
    private val immunizationRecordRepository: ImmunizationRecordRepository,
    private val immunizationForecastRepository: ImmunizationForecastRepository,
    private val immunizationRecommendationRepository: ImmunizationRecommendationRepository,
    private val commentsRepository: CommentRepository,
    private val healthVisitsRepository: HealthVisitsRepository,
    private val specialAuthorityRepository: SpecialAuthorityRepository
) : CoroutineWorker(context, workerParams) {

    var isApiFailed = false

    override suspend fun doWork(): Result {
        if (bcscAuthRepo.getPostLoginCheck() == PostLoginCheck.IN_PROGRESS.name ||
            !bcscAuthRepo.checkSession()
        ) {
            return Result.failure()
        }
        return fetchAuthRecords()
    }

    private suspend fun fetchAuthRecords(): Result {

        isApiFailed = false
        var vaccineRecordsResponse: Pair<VaccineRecordState, PatientVaccineRecord?>? = null
        var covidOrderResponse: List<CovidOrderWithCovidTestDto>? = null
        var medicationResponse: MedicationStatementResponse? = null
        var labOrdersResponse: List<LabOrderWithLabTestDto>? = null
        var immunizationDto: ImmunizationDto? = null
        var commentsResponse: List<CommentDto>? = null
        var dependentsList: List<DependentDto>? = null
        var healthVisitsResponse: List<HealthVisitsDto>? = null
        var specialAuthorityResponse: List<SpecialAuthorityDto>? = null

        try {

            val authParameters = bcscAuthRepo.getAuthParameters()
            var patient: PatientDto? = null
            var patientId = 0L

            try {
                val isHgServicesUp = mobileConfigRepository.getBaseUrl()
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
                    authParameters.first,
                    authParameters.second
                )
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

            try {
                vaccineRecordsResponse = fetchVaccineRecords(authParameters)
            } catch (e: Exception) {
                handleException(e)?.let { failureResult ->
                    return failureResult
                }
            }

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
                dependentsList = fetchDependents(authParameters)
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
            // Insert patient details
            patient?.let {
                patientId = patientRepository.insertAuthenticatedPatient(it)
            }
            // Insert vaccine records
            vaccineRecordsResponse?.second?.let {
                patientWithVaccineRecordRepository.insertAuthenticatedPatientsVaccineRecord(
                    patientId, it
                )
            }
            // Insert covid orders
            patientWithTestResultRepository.deletePatientTestRecords(patientId)
            covidOrderRepository.deleteByPatientId(patientId)
            covidOrderResponse?.forEach {
                it.covidOrder.patientId = patientId
                covidOrderRepository.insert(it.covidOrder)
                covidTestRepository.insert(it.covidTests)
            }
            // Insert medication records
            medicationResponse?.let {
                medicationRecordRepository.updateMedicationRecords(
                    it,
                    patientId
                )
            }
            // Insert lab orders
            labOrderRepository.delete(patientId)
            labOrdersResponse?.forEach {
                it.labOrder.patientId = patientId
                val id = labOrderRepository.insert(it.labOrder)
                it.labTests.forEach { test ->
                    test.labOrderId = id
                }
                labTestRepository.insert(it.labTests)
            }
            // Insert immunization records
            immunizationRecordRepository.delete(patientId)

            immunizationDto?.records?.forEach {
                it.immunizationRecord.patientId = patientId
                val id = immunizationRecordRepository.insert(it.immunizationRecord)

                it.immunizationForecast?.immunizationRecordId = id
                it.immunizationForecast?.let { forecast ->
                    immunizationForecastRepository.insert(
                        forecast
                    )
                }
            }

            immunizationDto?.recommendations?.forEach {
                it.patientId = patientId
                immunizationRecommendationRepository.insert(it)
            }

            // Insert comments
            commentsRepository.delete(true)
            commentsResponse?.let { commentsRepository.insert(it) }

            //Insert dependents
            dependentsList?.let { dependentsRepository.storeDependents(it) }

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

            if (isApiFailed) {
                notificationHelper.updateNotification(context.getString(R.string.notification_title_on_failed))
            } else {
                notificationHelper.updateNotification(context.getString(R.string.notification_title_on_success))
            }
        } catch (e: Exception) {
            // no implementation required.
            e.printStackTrace()
        }
        return Result.success()
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

    /*
    * Fetch comments
    * */
    private suspend fun fetchComments(authParameters: Pair<String, String>): List<CommentDto>? {
        var commentsResponse: List<CommentDto>?
        withContext(dispatcher) {
            commentsResponse = commentsRepository.getComments(
                authParameters.first,
                authParameters.second
            )
        }
        return commentsResponse
    }

    private suspend fun fetchDependents(authParameters: Pair<String, String>): List<DependentDto>? {
        var dependents: List<DependentDto>?
        withContext(dispatcher) {
            dependents = dependentsRepository.fetchAllDependents(
                token = authParameters.first, hdid = authParameters.second
            )
        }
        return dependents
    }

    /*
     * Fetch immunizations
     * */
    private suspend fun fetchImmunizations(authParameters: Pair<String, String>): ImmunizationDto? {
        var immunizationDto: ImmunizationDto?
        withContext(dispatcher) {
            immunizationDto =
                immunizationRecordRepository.fetchImmunization(
                    authParameters.first,
                    authParameters.second
                )
        }
        return immunizationDto
    }

    /*
     * Fetch lab test results
     * */
    private suspend fun fetchLabTestResults(authParameters: Pair<String, String>): List<LabOrderWithLabTestDto>? {
        var labOrdersResponse: List<LabOrderWithLabTestDto>?
        withContext(dispatcher) {
            labOrdersResponse =
                labOrderRepository.fetchLabOrders(
                    authParameters.first,
                    authParameters.second
                )
        }
        return labOrdersResponse
    }

    /*
    * Fetch medication records
    * */
    private suspend fun fetchMedicationResponse(authParameters: Pair<String, String>): MedicationStatementResponse? {
        var medicationResponse: MedicationStatementResponse?
        withContext(dispatcher) {
            medicationResponse = medicationRecordRepository.fetchMedicationStatement(
                authParameters.first,
                authParameters.second,
                encryptedPreferenceStorage.protectiveWord
            )
        }
        return medicationResponse
    }

    /*
    * Fetch vaccine records
    * */
    private suspend fun fetchVaccineRecords(authParameters: Pair<String, String>): Pair<VaccineRecordState, PatientVaccineRecord?>? {
        var vaccineRecordsResponse: Pair<VaccineRecordState, PatientVaccineRecord?>?
        withContext(dispatcher) {
            vaccineRecordsResponse = fetchVaccineRecordRepository.fetchVaccineRecord(
                authParameters.first,
                authParameters.second
            )
        }
        return vaccineRecordsResponse
    }

    /*
    * Fetch covid test results
    * */
    private suspend fun fetchCovidTestResults(authParameters: Pair<String, String>): List<CovidOrderWithCovidTestDto>? {
        var covidOrderResponse: List<CovidOrderWithCovidTestDto>?
        withContext(dispatcher) {
            covidOrderResponse = covidOrderRepository.fetchCovidOrders(
                authParameters.first,
                authParameters.second
            )
        }
        return covidOrderResponse
    }

    /**
     * Fetch health visits
     */
    private suspend fun fetchHealthVisits(authParameters: Pair<String, String>): List<HealthVisitsDto>? {
        var healthVisitsResponse: List<HealthVisitsDto>?
        withContext(dispatcher) {
            healthVisitsResponse =
                healthVisitsRepository.getHealthVisits(
                    authParameters.first,
                    authParameters.second
                )
        }
        return healthVisitsResponse
    }

    /**
     * Fetch special authority
     */
    private suspend fun fetchSpecialAuthority(authParameters: Pair<String, String>): List<SpecialAuthorityDto>? {
        var specialAuthorityResponse: List<SpecialAuthorityDto>?
        withContext(dispatcher) {
            specialAuthorityResponse =
                specialAuthorityRepository.getSpecialAuthority(
                    authParameters.first,
                    authParameters.second
                )
        }
        return specialAuthorityResponse
    }

    private fun handleQueueItException(e: java.lang.Exception): Result {
        return Result.failure(
            Data.Builder()
                .putString("queueItUrl", e.message.toString())
                .build()
        )
    }

    private fun respondToHgServicesDown(): Result {
        return Result.failure(
            Data.Builder()
                .putBoolean("isHgServicesUp", false)
                .build()
        )
    }
}
