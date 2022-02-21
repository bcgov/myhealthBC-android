package ca.bc.gov.repository.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import ca.bc.gov.common.R
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

const val WORK_RESULT = "WORK_RESULT"
const val CAN_NAVIGATE = "CAN_NAVIGATE"
const val PATIENT_ID = "PATIENT_ID"

@HiltWorker
class FetchAuthenticatedPatientDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val patientWithBCSCLoginRepository: PatientWithBCSCLoginRepository,
    private val bcscAuthRepo: BcscAuthRepo,
    private val patientRepository: PatientRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        notificationHelper.showNotification(
            context.getString(R.string.notification_title_while_fetching_data),
            context.getString(R.string.notification_message_while_fetching_data)
        )
        var patientId: Long
        val authParameters = bcscAuthRepo.getAuthParameters()
        var output: Data = workDataOf()
        try {
            withContext(dispatcher) {
                val patient = patientWithBCSCLoginRepository.getPatient(
                    authParameters.first,
                    authParameters.second
                )
                patientId = patientRepository.insertAuthenticatedPatient(patient)
                if (patientId > -1L) {
                    output = workDataOf(
                        CAN_NAVIGATE to true,
                        PATIENT_ID to patientId
                    )
                }
            }
            return Result.success(output)
        } catch (e: Exception) {
            notificationHelper.updateNotification(
                context.getString(R.string.notification_title_when_failed),
                context.getString(R.string.notification_message_when_failed)
            )
            return when (e) {
                is MustBeQueuedException -> {
                    output = workDataOf(WORK_RESULT to e.message)
                    Result.failure(output)
                }
                is MyHealthNetworkException -> {
                    Result.failure()
                }
                else -> {
                    Result.failure()
                }
            }
        }
    }
}
