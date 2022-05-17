package ca.bc.gov.repository.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ca.bc.gov.common.R
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.common.model.ProtectiveWordState
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
    private val mobileConfigRepository: MobileConfigRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (bcscAuthRepo.getPostLoginCheck() == PostLoginCheck.IN_PROGRESS.name) {
            return Result.failure()
        }
        fetchAuthRecords()
        return Result.success()
    }

    private suspend fun fetchAuthRecords() {
        var isApiFailed = false

        try {
            try {
                val response = mobileConfigRepository.getBaseUrl()
                encryptedPreferenceStorage.baseUrl = response.baseUrl
                encryptedPreferenceStorage.baseUrlIsOnline = response.online
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val authParameters = bcscAuthRepo.getAuthParameters()
            var patient: PatientDto? = null
            var patientId = 0L
            var vaccineRecordsResponse: Pair<VaccineRecordState, PatientVaccineRecord?>? = null
            var covidOrderResponse: List<CovidOrderWithCovidTestDto>? = null
            var medicationResponse: MedicationStatementResponse? = null
            var labOrdersResponse: List<LabOrderWithLabTestDto>? = null

            notificationHelper.showNotification(
                context.getString(R.string.notification_title_while_fetching_data)
            )

            try {
                patient = patientWithBCSCLoginRepository.getPatient(
                    authParameters.first,
                    authParameters.second
                )
            } catch (e: Exception) {
                isApiFailed = true
                e.printStackTrace()
            }

            /*
            * Fetch vaccine records
            * */
            try {
                withContext(dispatcher) {
                    vaccineRecordsResponse = fetchVaccineRecordRepository.fetchVaccineRecord(
                        authParameters.first,
                        authParameters.second
                    )
                }
            } catch (e: Exception) {
                isApiFailed = true
                e.printStackTrace()
            }

            /*
            * Fetch covid test results
            * */
            try {
                withContext(dispatcher) {
                    covidOrderResponse = covidOrderRepository.fetchCovidOrders(
                        authParameters.first,
                        authParameters.second
                    )
                }
            } catch (e: Exception) {
                isApiFailed = true
                e.printStackTrace()
            }

            /*
            * Fetch medication records
            * */
            try {
                withContext(dispatcher) {
                    medicationResponse = medicationRecordRepository.fetchMedicationStatement(
                        authParameters.first,
                        authParameters.second,
                        encryptedPreferenceStorage.protectiveWord
                    )
                }
            } catch (e: Exception) {
                if (e is ProtectiveWordException) {
                    encryptedPreferenceStorage.protectiveWordState =
                        ProtectiveWordState.PROTECTIVE_WORD_REQUIRED.value
                } else {
                    isApiFailed = true
                }
            }

            /*
            * Fetch lab test results
            * */
            try {
                withContext(dispatcher) {
                    labOrdersResponse =
                        labOrderRepository.fetchLabOrders(
                            authParameters.first,
                            authParameters.second
                        )
                }
            } catch (e: Exception) {
                isApiFailed = true
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

            if (isApiFailed) {
                notificationHelper.updateNotification(context.getString(R.string.notification_title_on_failed))
            } else {
                notificationHelper.updateNotification(context.getString(R.string.notification_title_on_success))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
