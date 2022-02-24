package ca.bc.gov.repository.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ca.bc.gov.common.R
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.PatientWithTestResultRepository
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
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    private val medicationRecordRepository: MedicationRecordRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val patientRepository: PatientRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val patientId: Long = inputData.getLong(PATIENT_ID, 0)

        if (patientId > 0 && patientRepository.isAuthenticatedPatient(patientId)) {
            //authenticated patient is available in DB means token is not expired
            fetchAuthRecords(patientId)
        }
        return Result.success()
    }

    private suspend fun fetchAuthRecords(patientId: Long) {
        val successApiMsgList = arrayListOf<String>()
        val failApiMsgList = arrayListOf<String>()

        val authParameters = bcscAuthRepo.getAuthParameters()
        notificationHelper.showNotification(
            context.getString(R.string.notification_title_while_fetching_data),
            context.getString(R.string.notification_message_while_fetching_data)
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
            successApiMsgList.add(context.getString(R.string.vaccine_records))
        } catch (e: Exception) {
            failApiMsgList.add(context.getString(R.string.vaccine_records))
            e.printStackTrace()
        }
        try {
            withContext(dispatcher) {
                fetchTestResultRepository.fetchAuthenticatedTestRecord(
                    patientId, authParameters.first,
                    authParameters.second
                )
            }
            successApiMsgList.add(context.getString(R.string.covid_test_result))
        } catch (e: Exception) {
            failApiMsgList.add(context.getString(R.string.covid_test_result))
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
            successApiMsgList.add(context.getString(R.string.medication_records))
        } catch (e: Exception) {
            failApiMsgList.add(context.getString(R.string.medication_records))
        }

        val notificationMsg = prepareNotificationMsg(successApiMsgList, failApiMsgList)
        notificationHelper.updateNotification(
            context.getString(R.string.notification_title_fetching_records_completed),
            notificationMsg
        )
    }

    private fun prepareNotificationMsg(
        successApiMsgList: ArrayList<String>,
        failApiMsgList: ArrayList<String>
    ): String {
        val notificationMsg = StringBuilder()
        if (successApiMsgList.isNotEmpty()) {
            notificationMsg.append(context.getString(R.string.retrieving))
            notificationMsg.append(successApiMsgList.first())
            successApiMsgList.subList(1, successApiMsgList.size).forEach {
                notificationMsg.append(",").append(it)
            }
            notificationMsg.append(context.getString(R.string.successful))
        }
        if (failApiMsgList.isNotEmpty()) {
            notificationMsg.append(context.getString(R.string.retrieving))
            notificationMsg.append(failApiMsgList.first())
            failApiMsgList.subList(1, failApiMsgList.size).forEach {
                notificationMsg.append(",").append(it)
            }
            notificationMsg.append(context.getString(R.string.failed))
        }

        return notificationMsg.toString()
    }
}
