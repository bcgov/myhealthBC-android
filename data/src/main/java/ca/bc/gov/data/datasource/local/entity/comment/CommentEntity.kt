package ca.bc.gov.data.datasource.local.entity.comment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "comments"
)
data class CommentEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "user_profile_id")
    val userProfileId: String? = null,
    val text: String? = null,
    @ColumnInfo(name = "entry_type_code")
    val entryTypeCode: String? = null,
    @ColumnInfo(name = "parent_entry_id")
    val parentEntryId: String? = null,
    val version: Int,
    @ColumnInfo(name = "created_date_time")
    val createdDateTime: Instant,
    @ColumnInfo(name = "created_by")
    val createdBy: String? = null,
    @ColumnInfo(name = "updated_date_time")
    val updatedDateTime: Instant,
    @ColumnInfo(name = "updated_by")
    val updatedBy: String? = null,
)
