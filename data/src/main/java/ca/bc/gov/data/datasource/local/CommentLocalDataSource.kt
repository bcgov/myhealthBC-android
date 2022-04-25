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

    suspend fun delete(parentEntryId: String?, isUploaded: Boolean) = commentDao.delete(parentEntryId, isUploaded)

    suspend fun deleteById(id: String) = commentDao.deleteById(id)

    suspend fun findCommentsByUploadFlag(isUploaded: Boolean): List<CommentDto> =
        commentDao.findCommentsByUploadFlag(isUploaded).map { it.toDto() }
}
