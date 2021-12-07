package ca.bc.gov.bchealth.model.network.responses.covidtests

data class Record(
    val collectionDateTime: String?,
    val lab: String?,
    val patientDisplayName: String?,
    val reportId: String?,
    val resultDateTime: String?,
    val resultDescription: String?,
    val resultLink: String?,
    val resultTitle: String?,
    val testName: String?,
    val testOutcome: String?,
    val testStatus: String?,
    val testType: String?
)
