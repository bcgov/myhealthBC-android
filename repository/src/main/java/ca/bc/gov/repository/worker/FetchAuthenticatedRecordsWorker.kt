package ca.bc.gov.repository.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.PatientWithTestResultRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.patient.PatientRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

const val WORK_RESULT = "WORK_RESULT"

@HiltWorker
class FetchAuthenticatedRecordsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val patientWithBCSCLoginRepository: PatientWithBCSCLoginRepository,
    private val fetchVaccineRecordRepository: FetchVaccineRecordRepository,
    private val fetchTestResultRepository: FetchTestResultRepository,
    private val bcscAuthRepo: BcscAuthRepo,
    private val patientWithVaccineRecordRepository: PatientWithVaccineRecordRepository,
    private val patientRepository: PatientRepository,
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        var patientId: Long
        val pair = bcscAuthRepo.getHdId()
        try {
            withContext(dispatcher) {
                val patient = patientWithBCSCLoginRepository.getPatient(pair.first, pair.second)
                patientId = patientRepository.insertAuthenticatedPatient(patient)
            }
        } catch (e: Exception) {
            return when (e) {
                is MustBeQueuedException -> {
                    val output: Data = workDataOf(WORK_RESULT to e.message)
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
        try {
            withContext(dispatcher) {
                val response = fetchVaccineRecordRepository.fetchAuthenticatedVaccineRecord(
                    pair.first,
                    pair.second
                )
                response.second?.let {
                    patientWithVaccineRecordRepository.insertAuthenticatedPatientsVaccineRecord(
                        patientId, it
                    )
                }
            }
        } catch (e: Exception) {
            return Result.failure()
        }
        try {
            withContext(dispatcher) {
                val response =
                    fetchTestResultRepository.fetchAuthenticatedTestRecord(pair.first, pair.second)
                for (i in response.indices) {
                    patientWithTestResultRepository.insertAuthenticatedTestResult(
                        patientId,
                        response[i]
                    )
                }
            }
        } catch (e: Exception) {
            return Result.failure()
        }

        return Result.success()
    }
}