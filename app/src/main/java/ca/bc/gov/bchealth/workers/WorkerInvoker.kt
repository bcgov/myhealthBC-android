package ca.bc.gov.bchealth.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import ca.bc.gov.repository.worker.FetchAuthenticatedHealthRecordsWorker

class WorkerInvoker(private val applicationContext: Context) {

    fun executeOneTimeDataFetch() {
        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequest
            .Builder(FetchAuthenticatedHealthRecordsWorker::class.java)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                oneTimeWorkRequest
            )
    }
}
