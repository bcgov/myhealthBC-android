package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto

/**
 * @author Pinakin Kansara
 */
data class TestResultWithRecordsDto(
    val testResultDto: TestResultDto,
    val testRecordDtos: List<TestRecordDto> = emptyList()
)
