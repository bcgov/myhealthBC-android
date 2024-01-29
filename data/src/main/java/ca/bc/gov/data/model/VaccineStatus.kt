package ca.bc.gov.data.model

/**
 * @author Pinakin Kansara
 */
data class VaccineStatus(
    val phn: String? = null,
    val qrCode: MediaMetaData?,
    val federalVaccineProof: MediaMetaData?
)
