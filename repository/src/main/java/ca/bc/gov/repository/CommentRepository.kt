package ca.bc.gov.repository

import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.data.datasource.local.CommentLocalDataSource
import ca.bc.gov.data.datasource.remote.CommentRemoteDataSource
import ca.bc.gov.repository.bcsc.BcscAuthRepo
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
        delete(parentEntryId)
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

    suspend fun delete(parentEntryId: String?) = commentLocalDataSource.delete(parentEntryId)

    suspend fun addComment(parentEntryId: String?, userProfileId: String?, comment: String): List<CommentDto> {
        val (token, hdid) = bcscAuthRepo.getAuthParameters()
        val comment = commentRemoteDataSource.addComment(parentEntryId, userProfileId, comment, hdid, token)
        insert(comment)
        return commentLocalDataSource.findCommentByParentEntryId(parentEntryId)
    }
}
