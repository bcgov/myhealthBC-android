package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.patient.PatientDto

/**
 * @author Pinakin Kansara
 */
data class PatientWithTestRecordDto(
    val patient: PatientDto,
    val testResultWithRecordsDto: List<TestResultWithRecordsDto>
)
