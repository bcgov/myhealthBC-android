package ca.bc.gov.repository.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.patient.PatientRepository
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

const val WORK_RESULT = "WORK_RESULT"
const val PATIENT = "PATIENT"

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
        val authParameters = bcscAuthRepo.getAuthParameters()
        var output: Data
        try {
            withContext(dispatcher) {
                val patient = patientWithBCSCLoginRepository.getPatient(
                    authParameters.first,
                    authParameters.second
                )
                output = workDataOf(PATIENT to Gson().toJson(patient))
            }
            return Result.success(output)
        } catch (e: Exception) {
            e.printStackTrace()
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