package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.patient.PatientDto

/**
 * @author Pinakin Kansara
 */
data class PatientAndVaccineRecord(
    val patientDto: PatientDto,
    val vaccineRecordDto: VaccineRecordDto?
)
