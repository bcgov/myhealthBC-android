package ca.bc.gov.repository.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.const.SERVER_ERROR_INCORRECT_PHN
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.PatientWithTestResultRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.patient.PatientRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    private val patientWithTestResultRepository: PatientWithTestResultRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        var patientId: Long
        val pair = bcscAuthRepo.getHdId()
        try {
            withContext(Dispatchers.IO) {
                val patient = patientWithBCSCLoginRepository.getPatient(pair.first, pair.second)
                patientId = patientRepository.insertAuthenticatedPatient(patient)
            }
        } catch (e: Exception) {
            return when (e) {
                is MyHealthNetworkException -> {
                    Result.failure()
                }
                else -> {
                    Result.failure()
                }
            }
        }
        try {
            withContext(Dispatchers.IO) {
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
            when (e) {
                is MyHealthNetworkException -> {
                    return when (e.errCode) {
                        SERVER_ERROR_DATA_MISMATCH -> {
                            Result.failure()
                        }
                        SERVER_ERROR_INCORRECT_PHN -> {
                            Result.failure()
                        }
                        else -> {
                            Result.failure()
                        }
                    }
                }
            }
        }
        try {
            withContext(Dispatchers.IO) {
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
            Log.i("RASHMI", "Exception ${e.message}")
            when (e) {
                is MustBeQueuedException -> {
                    Result.failure()
                }
                is MyHealthNetworkException -> {
                    when (e.errCode) {
                        SERVER_ERROR_DATA_MISMATCH -> {
                            Result.failure()
                        }
                        SERVER_ERROR_INCORRECT_PHN -> {
                            Result.failure()
                        }
                        else -> {
                            Result.failure()
                        }
                    }
                }
                else -> {
                    Result.failure()
                }
            }
        }

        return Result.success()
    }
}