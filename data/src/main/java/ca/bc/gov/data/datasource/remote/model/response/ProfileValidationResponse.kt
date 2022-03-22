package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.Error

data class ProfileValidationResponse(
    val pageIndex: Int?,
    val pageSize: Int?,
    val resourcePayload: Boolean?,
    val error: Error?,
    val resultStatus: Int?,
    val totalResultCount: Int?
)
