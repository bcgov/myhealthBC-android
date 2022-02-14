package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.patient.PatientDto

/**
 * @author Pinakin Kansara
 */
data class PatientWithMedicationRecordDto(
    val patient: PatientDto,
    val medicationRecord: List<MedicationRecordDto>
)
