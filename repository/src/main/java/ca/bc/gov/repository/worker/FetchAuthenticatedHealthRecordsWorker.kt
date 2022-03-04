package ca.bc.gov.repository.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ca.bc.gov.common.R
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.patient.PatientRepository
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
    private val fetchTestResultRepository: FetchTestResultRepository,
    private val bcscAuthRepo: BcscAuthRepo,
    private val patientWithVaccineRecordRepository: PatientWithVaccineRecordRepository,
    private val medicationRecordRepository: MedicationRecordRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val patientRepository: PatientRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val patientId: Long = inputData.getLong(PATIENT_ID, 0)

        if (patientId > 0 && patientRepository.isAuthenticatedPatient(patientId)) {
            // authenticated patient is available in DB means token is not expired
            fetchAuthRecords(patientId)
        }
        return Result.success()
    }

    private suspend fun fetchAuthRecords(patientId: Long) {
        var isApiFailed = false

        val authParameters = bcscAuthRepo.getAuthParameters()
        notificationHelper.showNotification(
            context.getString(R.string.notification_title_while_fetching_data)
        )
        try {
            withContext(dispatcher) {
                val response = fetchVaccineRecordRepository.fetchVaccineRecord(
                    authParameters.first,
                    authParameters.second
                )
                response.second?.let {
                    patientWithVaccineRecordRepository.insertAuthenticatedPatientsVaccineRecord(
                        patientId, it
                    )
                }
            }
        } catch (e: Exception) {
            isApiFailed = true
            e.printStackTrace()
        }
        try {
            withContext(dispatcher) {
                fetchTestResultRepository.fetchAuthenticatedTestRecord(
                    patientId, authParameters.first,
                    authParameters.second
                )
            }
        } catch (e: Exception) {
            isApiFailed = true
            e.printStackTrace()
        }
        try {
            withContext(dispatcher) {
                medicationRecordRepository.fetchMedicationStatement(
                    patientId,
                    authParameters.first,
                    authParameters.second
                )
            }
        } catch (e: Exception) {
            isApiFailed = true
        }

        if (isApiFailed) {
            notificationHelper.updateNotification(context.getString(R.string.notification_title_on_failed))
        } else {
            notificationHelper.updateNotification(context.getString(R.string.notification_title_on_success))
        }
    }
}
