package ca.bc.gov.repository.model

import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.patient.PatientDto

/**
 * @author Pinakin Kansara
 */
data class PatientVaccineRecord(
    val patientDto: PatientDto,
    val vaccineRecordDto: VaccineRecordDto
)
