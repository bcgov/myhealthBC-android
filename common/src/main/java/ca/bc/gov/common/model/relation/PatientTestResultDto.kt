package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto

/**
 * @author Pinakin Kansara
 */
data class PatientTestResultDto(
    val patientDto: PatientDto,
    val testResultDto: TestResultDto,
    val recordDtos: List<TestRecordDto>
)
