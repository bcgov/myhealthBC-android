package ca.bc.gov.data.remote.model.request

/**
 * @author Pinakin Kansara
 */
data class VaccineStatusRequest(
    val phn: String,
    val dateOfBirth: String,
    val dateOfVaccine: String
)

fun VaccineStatusRequest.toMap(): Map<String, String> {
    return hashMapOf(
        "phn" to phn,
        "dateOfBirth" to dateOfBirth,
        "dateOfVaccine" to dateOfVaccine
    )
}
