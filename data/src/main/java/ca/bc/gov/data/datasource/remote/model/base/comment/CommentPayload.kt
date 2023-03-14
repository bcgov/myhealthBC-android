package ca.bc.gov.data.datasource.remote.model.base.comment

/**
 * @author Pinakin Kansara
 */
data class CommentPayload(
    val id: String,
    val userProfileId: String?,
    val text: String?,
    val entryTypeCode: String?,
    val parentEntryId: String?,
    val version: Long,
    val createdDateTime: String,
    val createdBy: String?,
    val updatedDateTime: String,
    val updatedBy: String?,
)
