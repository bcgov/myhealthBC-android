package ca.bc.gov.repository.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.PatientWithTestResultRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.patient.PatientRepository
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/*
* Created by amit_metri on 16,February,2022
*/
@HiltWorker
class FetchAuthenticatedHealthRecordsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fetchVaccineRecordRepository: FetchVaccineRecordRepository,
    private val fetchTestResultRepository: FetchTestResultRepository,
    private val bcscAuthRepo: BcscAuthRepo,
    private val patientWithVaccineRecordRepository: PatientWithVaccineRecordRepository,
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    private val medicationRecordRepository: MedicationRecordRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val patientRepository: PatientRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        var patientId = -1L
        val patientJson = inputData.getString(PATIENT)

        if (!patientJson.isNullOrBlank()) {
            // clear all records related to patient Id
            val patient = Gson().fromJson(patientJson, PatientDto::class.java)
            try {
                withContext(dispatcher) {
                    patientId = patientRepository.insertAuthenticatedPatient(patient)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (patientId > -1L) {
                fetchAuthRecords(patientId)
            }
        }
        return Result.success()
    }

    private suspend fun fetchAuthRecords(patientId: Long) {
        val authParameters = bcscAuthRepo.getAuthParameters()
        try {
            withContext(dispatcher) {
                val response = fetchVaccineRecordRepository.fetchAuthenticatedVaccineRecord(
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
            e.printStackTrace()
        }
        try {
            withContext(dispatcher) {
                val response =
                    fetchTestResultRepository.fetchAuthenticatedTestRecord(
                        authParameters.first,
                        authParameters.second
                    )
                for (i in response.indices) {
                    patientWithTestResultRepository.insertAuthenticatedTestResult(
                        patientId,
                        response[i]
                    )
                }
            }
        } catch (e: Exception) {
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
            e.printStackTrace()
        }
    }
}
