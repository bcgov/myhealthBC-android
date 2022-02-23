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
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val successApiMsgList = arrayListOf<String>()
        val failApiMsgList = arrayListOf<String>()

        val patientId: Long = inputData.getLong(PATIENT_ID, -1L)
        val authParameters = bcscAuthRepo.getAuthParameters()
        if (patientId > -1L) {
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
                // return Result.failure()
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
                successApiMsgList.add(context.getString(R.string.covid_test_result))
            } catch (e: Exception) {
                failApiMsgList.add(context.getString(R.string.covid_test_result))
                // return Result.failure()
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
                // return Result.failure()
            }

            val notificationMsg = prepareNotificationMsg(successApiMsgList, failApiMsgList)
            notificationHelper.updateNotification(
                context.getString(R.string.notification_title_fetching_records_completed),
                notificationMsg
            )
        }
        return Result.success()
    }

    private fun prepareNotificationMsg(successApiMsgList: ArrayList<String>, failApiMsgList: ArrayList<String>): String {
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
