package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.AuthenticatedCovidTestDto
import ca.bc.gov.common.model.LabResultDto
import ca.bc.gov.common.model.OrderDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.utils.formatInPattern
import ca.bc.gov.common.utils.formattedStringToDateTime
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toDateTime
import ca.bc.gov.data.remote.model.base.AuthenticatedCovidTestPayload
import ca.bc.gov.data.remote.model.base.CovidTestRecord
import ca.bc.gov.data.remote.model.base.LabResult
import ca.bc.gov.data.remote.model.base.Order

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

fun AuthenticatedCovidTestPayload.toPayloadDto() = AuthenticatedCovidTestDto(
    loaded = loaded,
    order = orders.map { it.toOrderDto() },
    retryInMilli = retryInMilli
)

fun Order.toOrderDto() = OrderDto(
    additionalData = additionalData,
    id = id,
    labResult = labResults?.map { it.toLabResultDto() },
    location = location,
    messageDateTime = messageDateTime,
    messageId = messageId,
    orderingProviderIds = orderingProviderIds,
    orderingProviders = orderingProviders,
    ormOrOru = ormOrOru,
    phn = phn,
    reportAvailable = reportAvailable,
    reportingLab = reportingLab
)

fun LabResult.toLabResultDto() = LabResultDto(
    collectedDateTime = collectedDateTime,
    id = id,
    labResultOutcome = labResultOutcome,
    loInc = loInc,
    loIncName = loIncName,
    outOfRange = outOfRange,
    receivedDateTime = receivedDateTime,
    resultDateTime = resultDateTime,
    resultDescription = resultDescription,
    resultLink = resultLink,
    testStatus = testStatus,
    testType = testType
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