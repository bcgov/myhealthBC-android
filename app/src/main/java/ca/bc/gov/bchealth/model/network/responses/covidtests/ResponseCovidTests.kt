package ca.bc.gov.bchealth.model.network.responses.covidtests

data class ResponseCovidTests(
    val pageIndex: Int?,
    val pageSize: Int?,
    val resourcePayload: ResourcePayload?,
    val resultError: ResultError?,
    val resultStatus: Int?,
    val totalResultCount: Int?
)
