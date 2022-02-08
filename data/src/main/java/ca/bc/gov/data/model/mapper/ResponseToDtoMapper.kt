package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.utils.formatInPattern
import ca.bc.gov.common.utils.formattedStringToDateTime
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toDateTime
import ca.bc.gov.data.remote.model.base.LabResult
import ca.bc.gov.data.remote.model.base.covidtest.CovidTestRecord

fun CovidTestRecord.toTestRecord() = TestRecordDto(
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

fun LabResult.toTestRecord() = TestRecordDto(
    id = id,
    labName = "",
    collectionDateTime = collectedDateTime!!.formatInPattern().toDate(),
    resultDateTime = resultDateTime.formattedStringToDateTime(),
    testName = loIncName ?: "",
    testOutcome = labResultOutcome ?: "",
    testType = testType,
    testStatus = testStatus ?: "",
    resultTitle = "",
    resultDescription = resultDescription,
    resultLink = resultLink
)
