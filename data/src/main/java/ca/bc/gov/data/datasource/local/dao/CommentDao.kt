package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.comment.CommentEntity
import kotlinx.coroutines.flow.Flow

/**
 * @author Pinakin Kansara
 */
@Dao
interface CommentDao : BaseDao<CommentEntity> {

    @Query("SELECT * FROM comments")
    fun getCommentsFlow(): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE id = :id")
    suspend fun findById(id: String): CommentEntity?

    @Query("SELECT * FROM comments WHERE user_profile_id = :hdid AND parent_entry_id = :parentEntryId")
    suspend fun findByPatientIdAndParentEntryId(
        hdid: String,
        parentEntryId: String
    ): List<CommentEntity>

    @Query("SELECT * FROM comments WHERE parent_entry_id = :parentEntryId")
    suspend fun findCommentByParentEntryId(parentEntryId: String?): List<CommentEntity>

    @Query("DELETE FROM comments WHERE parent_entry_id = :parentEntryId")
    suspend fun delete(parentEntryId: String?)
}
