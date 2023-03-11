package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.common.model.SyncStatus
import ca.bc.gov.data.datasource.local.entity.comment.CommentEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface CommentDao : BaseDao<CommentEntity> {

    @Query("SELECT * FROM comments WHERE parent_entry_id = :parentEntryId")
    suspend fun findCommentByParentEntryId(parentEntryId: String?): List<CommentEntity>

    @Query("DELETE FROM comments WHERE parent_entry_id = :parentEntryId AND sync_status = :syncStatus")
    suspend fun delete(parentEntryId: String?, syncStatus: SyncStatus)

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM comments WHERE sync_status != :syncStatus")
    suspend fun findExcept(syncStatus: SyncStatus): List<CommentEntity>

    @Query("DELETE FROM comments WHERE sync_status = :syncStatus")
    suspend fun delete(syncStatus: SyncStatus)

    @Query("UPDATE comments SET text=:content , sync_status=:syncStatus WHERE id = :commentId")
    suspend fun updateComment(commentId: String, content: String, syncStatus: SyncStatus)
}
