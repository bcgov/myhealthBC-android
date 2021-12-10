package ca.bc.gov.bchealth.model.network.responses.covidtests

data class ResultError(
    val actionCode: String?,
    val errorCode: String?,
    val resultMessage: String?,
    val traceId: String?
)
