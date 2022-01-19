package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.test.TestRecord
import ca.bc.gov.common.utils.toDateTime
import ca.bc.gov.data.remote.model.base.CovidTestRecord

fun CovidTestRecord.toTestRecord() = TestRecord(
    id = reportId,
    labName = labName,
    collectionDateTime = collectionDateTime.toDateTime(),
    resultDateTime = resultDateTime.toDateTime(),
    testName = testName,
    testOutcome = testOutcome,
    testType = testType,
    testStatus = testStatus,
    resultTitle = resultTitle,
    resultDescription = resultDescription,
    resultLink = resultLink
)
