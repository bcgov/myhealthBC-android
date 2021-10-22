package ca.bc.gov.bchealth.model

/**
 * [ImmunizationRecord]
 *
 * @author Pinakin Kansara
 */
data class ImmunizationRecord(
    val name: String,
    val birthDate: String?,
    val status: ImmunizationStatus,
    val issueDate: Long
)
