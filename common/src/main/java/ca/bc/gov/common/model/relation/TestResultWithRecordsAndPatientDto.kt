package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.patient.PatientDto

data class TestResultWithRecordsAndPatientDto(
    val testResultWithRecords: TestResultWithRecordsDto,
    val patient: PatientDto
)
