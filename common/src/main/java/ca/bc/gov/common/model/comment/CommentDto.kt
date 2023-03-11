package ca.bc.gov.common.model.comment

import ca.bc.gov.common.model.SyncStatus
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class CommentDto(
    val id: String,
    val userProfileId: String?,
    val text: String?,
    val entryTypeCode: String?,
    val parentEntryId: String?,
    val version: Long,
    val createdDateTime: Instant,
    val createdBy: String?,
    val updatedDateTime: Instant,
    val updatedBy: String?,
    var syncStatus: SyncStatus = SyncStatus.UP_TO_DATE
)
