package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.patient.PatientDto

/**
 * @author Pinakin Kansara
 */
data class PatientWithTestResultsAndRecordsDto(
    val patient: PatientDto,
    val testResultWithRecords: List<TestResultWithRecordsDto> = emptyList()
)
