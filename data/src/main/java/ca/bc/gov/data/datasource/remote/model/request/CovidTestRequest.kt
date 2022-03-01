package ca.bc.gov.data.datasource.remote.model.request

/**
 * @author Pinakin Kansara
 */
data class CovidTestRequest(
    val phn: String,
    val dateOfBirth: String,
    val collectionDate: String
)

fun CovidTestRequest.toMap(): Map<String, String> {
    return hashMapOf(
        "phn" to phn,
        "dateOfBirth" to dateOfBirth,
        "collectionDate" to collectionDate
    )
}
