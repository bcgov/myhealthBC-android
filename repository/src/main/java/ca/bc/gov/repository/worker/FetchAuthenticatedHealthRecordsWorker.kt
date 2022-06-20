package ca.bc.gov.repository.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import ca.bc.gov.common.R
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.datasource.remote.model.response.MedicationStatementResponse
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.PatientWithTestResultRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.bcsc.PostLoginCheck
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.immunization.ImmunizationForecastRepository
import ca.bc.gov.repository.immunization.ImmunizationRecordRepository
import ca.bc.gov.repository.labtest.LabOrderRepository
import ca.bc.gov.repository.labtest.LabTestRepository
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.qr.VaccineRecordState
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
        var immunizationResponse: List<ImmunizationRecordWithForecastDto>? = null

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
                if (e is MustBeQueuedException && e.message.toString().isNotBlank()) {
                    return handleQueueItException(e)
                } else {
                    isApiFailed = true
                }
            }

            try {
                vaccineRecordsResponse = fetchVaccineRecords(authParameters)
            } catch (e: Exception) {
                if (e is MustBeQueuedException && e.message.toString().isNotBlank()) {
                    return handleQueueItException(e)
                } else {
                    isApiFailed = true
                }
            }

            try {
                covidOrderResponse = fetchCovidTestResults(authParameters)
            } catch (e: Exception) {
                if (e is MustBeQueuedException && e.message.toString().isNotBlank()) {
                    return handleQueueItException(e)
                } else {
                    isApiFailed = true
                }
            }

            try {
                medicationResponse = fetchMedicationResponse(authParameters)
            } catch (e: Exception) {
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
                if (e is MustBeQueuedException && e.message.toString().isNotBlank()) {
                    return handleQueueItException(e)
                } else {
                    isApiFailed = true
                }
            }

            /*
             * Fetch Immunization record
             */
            try {
                immunizationResponse = fetchImmunisations(authParameters)
            } catch (e: Exception) {
                if (e is MustBeQueuedException && e.message.toString().isNotBlank()) {
                    return handleQueueItException(e)
                } else {
                    isApiFailed = true
                }
            }

            /*
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
            immunizationResponse?.forEach {
                it.immunizationRecord.patientId = patientId
                val id = immunizationRecordRepository.insert(it.immunizationRecord)
                it.immunizationForecast?.immunizationRecordId = id
                it.immunizationForecast?.let { forecast ->
                    immunizationForecastRepository.insert(
                        forecast
                    )
                }
            }

            if (isApiFailed) {
                notificationHelper.updateNotification(context.getString(R.string.notification_title_on_failed))
            } else {
                notificationHelper.updateNotification(context.getString(R.string.notification_title_on_success))
            }
        } catch (e: Exception) {
            // no implementation required.
        }
        return Result.success()
    }

    /*
     * Fetch immunisations
     * */
    private suspend fun fetchImmunisations(authParameters: Pair<String, String>): List<ImmunizationRecordWithForecastDto>? {
        var immunisationResponse: List<ImmunizationRecordWithForecastDto>?
        withContext(dispatcher) {
            immunisationResponse =
                immunizationRecordRepository.fetchImmunization(
                    authParameters.first,
                    authParameters.second
                )
        }
        return immunisationResponse
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
