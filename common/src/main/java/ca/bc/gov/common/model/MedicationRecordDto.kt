package ca.bc.gov.common.model

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class MedicationRecordDto(
    val id: Long = 0,
    val patientId: Long,
    val practitionerIdentifier: String?,
    val prescriptionStatus: String?,
    val practitionerSurname: String?,
    val dispenseDate: Instant,
    val directions: String?,
    val dateEntered: Instant,
    val dataSource: DataSource
)
