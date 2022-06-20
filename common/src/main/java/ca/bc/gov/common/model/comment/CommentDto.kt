package ca.bc.gov.common.model.comment

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
    val version: Int,
    val createdDateTime: Instant,
    val createdBy: String?,
    val updatedDateTime: Instant,
    val updatedBy: String?,
    var isUploaded: Boolean = true
)
