package ca.bc.gov.repository.model

import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.repository.qr.VaccineRecordState

/**
 * @author Pinakin Kansara
 */
data class PatientVaccineRecord(
    val patientDto: PatientDto,
    val vaccineRecordDto: VaccineRecordDto
)

data class PatientVaccineRecordsState(
    val patientId: Long,
    val vaccineRecordState: VaccineRecordState,
    val patientVaccineRecord: PatientVaccineRecord?
)
