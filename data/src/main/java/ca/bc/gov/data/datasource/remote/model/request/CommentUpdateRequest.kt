package ca.bc.gov.data.datasource.remote.model.request

data class CommentUpdateRequest(
    val id: String,
    val text: String,
    val parentEntryId: String?,
    val userProfileId: String?,
    val entryTypeCode: String?,
    val version: Long
)
