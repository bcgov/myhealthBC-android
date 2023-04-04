package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.SyncStatus
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

    suspend fun findCommentByParentEntryId(parentEntryId: String?): List<CommentDto> {
        val list = commentDao.findCommentByParentEntryId(parentEntryId)
            .map { it.toDto() }
            .toMutableList()
        list.sortBy { it.createdDateTime }
        return list
    }

    suspend fun insert(comment: CommentDto): Long {
        return commentDao.insert(comment.toEntity())
    }

    suspend fun insert(comments: List<CommentDto>): List<Long> {
        return commentDao.insert(comments.map { it.toEntity() })
    }

    suspend fun updateComment(commentId: String, content: String, syncStatus: SyncStatus) {
        return commentDao.updateComment(commentId, content, syncStatus)
    }

    suspend fun delete(parentEntryId: String?, syncStatus: SyncStatus) =
        commentDao.delete(parentEntryId, syncStatus)

    suspend fun deleteById(id: String) = commentDao.deleteById(id)

    suspend fun findNonSyncedComments(): List<CommentDto> =
        commentDao.findExcept(SyncStatus.UP_TO_DATE).map { it.toDto() }

    suspend fun delete(syncStatus: SyncStatus) = commentDao.delete(syncStatus)
}
