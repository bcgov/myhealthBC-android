package ca.bc.gov.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ca.bc.gov.common.model.SyncStatus
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.data.datasource.local.CommentLocalDataSource
import ca.bc.gov.data.datasource.remote.CommentRemoteDataSource
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.worker.SyncCommentsWorker
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */

const val SYNC_COMMENTS = "SYNC_COMMENTS"

class CommentRepository @Inject constructor(
    private val commentRemoteDataSource: CommentRemoteDataSource,
    private val commentLocalDataSource: CommentLocalDataSource,
    private val bcscAuthRepo: BcscAuthRepo,
    private val applicationContext: Context
) {

    suspend fun getComments(token: String, hdid: String): List<CommentDto> {
        return commentRemoteDataSource.fetchComments(hdid, token)
    }

    suspend fun getLocalComments(parentEntryId: String?): List<CommentDto> {
        return commentLocalDataSource.findCommentByParentEntryId(parentEntryId)
    }

    suspend fun insert(comment: CommentDto): Long {
        return commentLocalDataSource.insert(comment)
    }

    suspend fun insert(comments: List<CommentDto>): List<Long> {
        return commentLocalDataSource.insert(comments)
    }

    suspend fun delete(parentEntryId: String?, syncStatus: SyncStatus) =
        commentLocalDataSource.delete(parentEntryId, syncStatus)

    suspend fun delete(syncStatus: SyncStatus) = commentLocalDataSource.delete(syncStatus)

    suspend fun deleteById(id: String) = commentLocalDataSource.deleteById(id)

    suspend fun addComment(
        parentEntryId: String?,
        comment: String,
        entryTypeCode: String
    ): List<CommentDto> {
        val id = UUID.randomUUID().toString()
        val commentDto = CommentDto(
            id,
            null,
            comment,
            entryTypeCode,
            parentEntryId,
            0,
            Instant.now(),
            null,
            Instant.now(),
            null,
            SyncStatus.INSERT
        )
        insert(commentDto)
        enqueueSyncCommentsWorker()

        return getLocalComments(parentEntryId)
    }

    suspend fun syncComment(commentDto: CommentDto) {
        val authParametersDto = bcscAuthRepo.getAuthParametersDto()
        val comment = commentRemoteDataSource.addComment(
            commentDto.parentEntryId,
            commentDto.text ?: "",
            commentDto.entryTypeCode,
            authParametersDto.hdid,
            authParametersDto.token
        )
        deleteById(commentDto.id)
        comment.syncStatus = SyncStatus.UP_TO_DATE
        insert(comment)
    }

    suspend fun updateComment(commentDto: CommentDto): List<CommentDto> {
        val authParametersDto = bcscAuthRepo.getAuthParametersDto()
        val updatedComment = commentRemoteDataSource.updateComment(
            commentDto,
            authParametersDto.hdid,
            authParametersDto.token
        )
        deleteById(commentDto.id)
        updatedComment.syncStatus = SyncStatus.UP_TO_DATE
        insert(updatedComment)

        return getLocalComments(commentDto.parentEntryId)
    }

    suspend fun findCommentsBySyncStatus(syncStatus: SyncStatus) =
        commentLocalDataSource.findCommentsBySyncStatus(syncStatus)

    private fun enqueueSyncCommentsWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWorkRequest =
            OneTimeWorkRequest.Builder(SyncCommentsWorker::class.java)
                .setConstraints(constraints)
                .build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniqueWork(
            SYNC_COMMENTS,
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    }
}
