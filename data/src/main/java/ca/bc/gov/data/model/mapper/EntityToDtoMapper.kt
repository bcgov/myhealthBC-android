package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.relation.PatientWithVaccineRecordDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.TestRecordEntity
import ca.bc.gov.data.local.entity.TestResultEntity
import ca.bc.gov.data.local.entity.VaccineDoseEntity
import ca.bc.gov.data.local.entity.VaccineRecordEntity
import ca.bc.gov.data.local.entity.relations.PatientWithVaccineRecord
import ca.bc.gov.data.local.entity.relations.TestResultWithRecord

fun PatientEntity.toDto() = PatientDto(
    id, firstName, lastName, dateOfBirth, phn
)

fun TestResultEntity.toDto() = TestResultDto(
    id, patientId, collectionDate
)

fun TestRecordEntity.toDto() = TestRecordDto(
    id,
    testResultId,
    labName,
    collectionDateTime,
    resultDateTime,
    testName,
    testType,
    testOutcome,
    testStatus,
    resultTitle,
    resultDescription.split(" "),
    resultLink
)

fun VaccineDoseEntity.toDto() = VaccineDoseDto(
    id,
    vaccineRecordId,
    productName, providerName, lotNumber, date
)

fun VaccineRecordEntity.toDto() = VaccineRecordDto(
    id,
    patientId,
    qrIssueDate,
    status,
    qrCodeImage = null,
    shcUri,
    federalPass,
    dataSource
)

fun TestResultWithRecord.toDto() = TestResultWithRecordsDto(
    testResultDto = testResult.toDto(),
    testRecordDtos = testRecords.map { it.toDto() }
)

fun PatientWithVaccineRecord.toDto() = PatientWithVaccineRecordDto(
    patientDto = patient.toDto(),
    vaccineRecordDto = vaccineRecord?.toDto()
)
