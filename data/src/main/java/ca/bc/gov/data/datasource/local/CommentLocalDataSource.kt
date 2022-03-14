package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.data.datasource.local.dao.CommentDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class CommentLocalDataSource @Inject constructor(
    private val commentDao: CommentDao
) {

    suspend fun findCommentByParentEntryId(parentEntryId: String?): List<CommentDto> =
        commentDao.findCommentByParentEntryId(parentEntryId).map { it.toDto() }

    suspend fun findCommentByPatientIdAndParentEntryId(hdid: String, parentEntryId: String) =
        commentDao.findByPatientIdAndParentEntryId(hdid, parentEntryId)

    suspend fun insert(comment: CommentDto): Long {
        return commentDao.insert(comment.toEntity())
    }

    suspend fun insert(comments: List<CommentDto>): List<Long> {
        return commentDao.insert(comments.map { it.toEntity() })
    }

    suspend fun delete(parentEntryId: String?) = commentDao.delete(parentEntryId)
}
