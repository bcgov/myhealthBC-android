package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.CreatePatientDto
import ca.bc.gov.common.model.CreateVaccineDoseDto
import ca.bc.gov.common.model.CreateVaccineRecordDto
import ca.bc.gov.common.model.VaccineRecord
import ca.bc.gov.common.model.test.TestRecord
import ca.bc.gov.common.model.test.TestResult
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.TestRecordEntity
import ca.bc.gov.data.local.entity.TestResultEntity
import ca.bc.gov.data.local.entity.VaccineDoseEntity
import ca.bc.gov.data.local.entity.VaccineRecordEntity

fun CreatePatientDto.toEntity() = PatientEntity(
    firstName = firstName.uppercase(),
    lastName = lastName.uppercase(),
    dateOfBirth = dateOfBirth,
    phn = phn,
    patientOrder = Long.MAX_VALUE
)

fun CreateVaccineDoseDto.toEntity() = VaccineDoseEntity(
    vaccineRecordId = vaccineRecordId,
    productName = productName,
    providerName = providerName,
    lotNumber = lotNumber,
    date = date
)

fun CreateVaccineRecordDto.toEntity() = VaccineRecordEntity(
    patientId = patientId,
    qrIssueDate = qrIssueDate,
    status = status,
    shcUri = shcUri,
    federalPass = federalPass,
    dataSource = dataSource
)

fun TestResult.toEntity() = TestResultEntity(
    id,
    patientId,
    collectionDate
)

fun VaccineRecord.toEntity() = VaccineRecordEntity(
    id,
    patientId,
    qrIssueDate,
    status,
    shcUri!!,
    federalPass,
    mode
)

fun TestRecord.toEntity() = TestRecordEntity(
    id = id,
    testResultId = testResultId,
    labName = labName,
    collectionDateTime = collectionDateTime,
    resultDateTime = resultDateTime,
    testName = testName,
    testType = testType,
    testOutcome = testOutcome,
    testStatus = testStatus,
    resultTitle = resultTitle,
    resultLink = resultLink,
    resultDescription = resultDescription.joinToString { " " }
)
