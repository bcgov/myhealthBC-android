package ca.bc.gov.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
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

    suspend fun delete(parentEntryId: String?, isUploaded: Boolean) =
        commentLocalDataSource.delete(parentEntryId, isUploaded)

    suspend fun delete(isUploaded: Boolean) =
        commentLocalDataSource.delete(isUploaded)

    suspend fun deleteById(id: String) = commentLocalDataSource.deleteById(id)

    suspend fun addComment(
        parentEntryId: String?,
        comment: String,
        entryTypeCode: String
    ): List<CommentDto> {
        val (token, hdid) = bcscAuthRepo.getAuthParameters()
        val id = UUID.randomUUID().toString()
        val commentDto = CommentDto(
            id,
            hdid,
            comment,
            entryTypeCode,
            parentEntryId,
            0,
            Instant.now(),
            hdid,
            Instant.now(),
            hdid,
            false
        )
        insert(commentDto)
        enqueueSyncCommentsWorker()
        return commentLocalDataSource.findCommentByParentEntryId(parentEntryId)
    }

    suspend fun syncComment(commentDto: CommentDto) {
        val (token, hdid) = bcscAuthRepo.getAuthParameters()
        val comment = commentRemoteDataSource.addComment(
            commentDto.parentEntryId,
            commentDto.text ?: "",
            commentDto.entryTypeCode,
            hdid,
            token
        )
        deleteById(commentDto.id)
        comment.isUploaded = true
        insert(comment)
    }

    suspend fun findCommentsByUploadFlag(isUploaded: Boolean) =
        commentLocalDataSource.findCommentsByUploadFlag(isUploaded)

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

enum class CommentEntryTypeCode(val value: String) {
    MEDICATION("Med")
}
