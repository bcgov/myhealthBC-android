package ca.bc.gov.repository

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ca.bc.gov.repository.worker.FetchAuthenticatedRecordsWorker

class FetchAuthenticatedRecordsRepository(private val context: Context) {

    fun fetchAuthenticatedRecords() {
        val workRequest = OneTimeWorkRequestBuilder<FetchAuthenticatedRecordsWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}