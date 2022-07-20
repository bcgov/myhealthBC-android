package ca.bc.gov.data.datasource.remote.model.base.immunization

/**
 * @author Pinakin Kansara
 */
data class ImmunizationRecord(
    val id: String?,
    val dateOfImmunization: String,
    val status: String?,
    val valid: Boolean,
    val providerOrClinic: String?,
    val targetedDisease: String?,
    val immunization: Immunization,
    val forecast: Forecast?
)
