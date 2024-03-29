package ca.bc.gov.repository.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ca.bc.gov.repository.CommentRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.bcsc.PostLoginCheck
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * @author: Created by Rashmi Bambhania on 25,April,2022
 */
@HiltWorker
class SyncCommentsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bcscAuthRepo: BcscAuthRepo,
    private val commentRepository: CommentRepository
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        if (bcscAuthRepo.getPostLoginCheck() == PostLoginCheck.IN_PROGRESS.name) {
            return Result.retry()
        }
        var result = Result.success()

        val commentDtoList = commentRepository.findNonSyncedComments()
        commentDtoList.forEach { commentDto ->
            try {
                commentRepository.syncComment(commentDto)
            } catch (e: Exception) {
                e.printStackTrace()
                result = Result.retry()
            }
        }

        return result
    }
}
