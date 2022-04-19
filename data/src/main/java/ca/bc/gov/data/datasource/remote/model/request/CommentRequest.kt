package ca.bc.gov.data.datasource.remote.model.request

import java.time.Instant

/**
 * @author: Created by Rashmi Bambhania on 18,April,2022
 */
data class CommentRequest(
    val id: String,
    val text: String,
    val parentEntryId: String,
    val userProfileId: String,
    val entryTypeCode: String,
    val version: Int,
    val createdDateTime: Instant = Instant.now()
)
