package ca.bc.gov.bchealth.model.network.responses.covidtests

data class ResourcePayload(
    val loaded: Boolean,
    val records: List<Record>?,
    val retryin: Int
)
