package ca.bc.gov.data.datasource.remote.model.request

/**
 * @author: Created by Rashmi Bambhania on 18,April,2022
 */
data class CommentRequest(
    val text: String,
    val parentEntryId: String?,
    val userProfileId: String?,
    val entryTypeCode: String?,
    // val createdDateTime: String,
)
