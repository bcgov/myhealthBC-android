package ca.bc.gov.repository

import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.data.datasource.local.CommentLocalDataSource
import ca.bc.gov.data.datasource.remote.CommentRemoteDataSource
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class CommentRepository @Inject constructor(
    private val commentRemoteDataSource: CommentRemoteDataSource,
    private val commentLocalDataSource: CommentLocalDataSource,
    private val bcscAuthRepo: BcscAuthRepo
) {

    suspend fun getComments(parentEntryId: String?): List<CommentDto> {
        delete(parentEntryId, true)
        val (token, hdid) = bcscAuthRepo.getAuthParameters()
        val comments = commentRemoteDataSource.fetchComments(parentEntryId, hdid, token)
        insert(comments)
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
        syncComment(commentDto)
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
}
