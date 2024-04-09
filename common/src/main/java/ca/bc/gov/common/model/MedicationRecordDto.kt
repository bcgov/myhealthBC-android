package ca.bc.gov.common.model

/**
 * @author Pinakin Kansara
 */
data class MedicationRecordDto(
    val id: Long = 0,
    var patientId: Long = -1,
    val prescriptionIdentifier: String?,
    val prescriptionStatus: String?,
    val practitionerSurname: String?,
    val dispenseDate: String,
    val directions: String?,
    val dataSource: DataSource
)
