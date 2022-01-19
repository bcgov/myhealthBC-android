package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.VaccineDose
import ca.bc.gov.common.model.VaccineRecord
import ca.bc.gov.common.model.patient.Patient
import ca.bc.gov.common.model.relation.PatientAndVaccineRecord
import ca.bc.gov.common.model.relation.TestResultWithRecords
import ca.bc.gov.common.model.test.TestRecord
import ca.bc.gov.common.model.test.TestResult
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.TestRecordEntity
import ca.bc.gov.data.local.entity.TestResultEntity
import ca.bc.gov.data.local.entity.VaccineDoseEntity
import ca.bc.gov.data.local.entity.VaccineRecordEntity
import ca.bc.gov.data.local.entity.relations.PatientWithVaccineRecord
import ca.bc.gov.data.local.entity.relations.TestResultWithRecord

fun PatientEntity.toDto() = Patient(
    id, firstName, lastName, dateOfBirth, phn
)

fun TestResultEntity.toDto() = TestResult(
    id, patientId, collectionDate
)

fun TestRecordEntity.toDto() = TestRecord(
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

fun VaccineDoseEntity.toDto() = VaccineDose(
    id,
    vaccineRecordId,
    productName, providerName, lotNumber, date
)

fun VaccineRecordEntity.toDto() = VaccineRecord(
    id,
    patientId,
    qrIssueDate,
    status,
    qrCodeImage = null,
    shcUri,
    federalPass,
    dataSource
)

fun TestResultWithRecord.toDto() = TestResultWithRecords(
    testResult = testResult.toDto(),
    testRecords = testRecords.map { it.toDto() }
)

fun PatientWithVaccineRecord.toDto() = PatientAndVaccineRecord(
    patient = patient.toDto(),
    vaccineRecord = vaccineRecord?.toDto()
)
