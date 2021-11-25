package ca.bc.gov.bchealth.model

import ca.bc.gov.bchealth.model.healthpasses.qr.Entry

/**
 * [ImmunizationRecord]
 *
 * @author Pinakin Kansara
 */
data class ImmunizationRecord(
    val name: String,
    val birthDate: String?,
    val status: ImmunizationStatus,
    val issueDate: Long,
    val occurrenceDateTime: String?,
    val immunizationEntries: List<Entry>?
)
