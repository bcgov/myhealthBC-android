package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.vaccine.Media

data class LabTestPdfResponse(
    val pageIndex: Int?,
    val pageSize: Int?,
    val resourcePayload: Media?,
    val error: Error?,
    val resultStatus: Int?,
    val totalResultCount: Int?
)
