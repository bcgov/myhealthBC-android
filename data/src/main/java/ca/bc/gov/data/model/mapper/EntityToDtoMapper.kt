package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.relation.PatientWithMedicationRecordDto
import ca.bc.gov.common.model.relation.PatientWithTestResultsAndRecordsDto
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsAndPatientDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsDto
import ca.bc.gov.common.model.relation.VaccineWithDosesDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto
import ca.bc.gov.data.local.entity.MedicationRecordEntity
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.TestRecordEntity
import ca.bc.gov.data.local.entity.TestResultEntity
import ca.bc.gov.data.local.entity.VaccineDoseEntity
import ca.bc.gov.data.local.entity.VaccineRecordEntity
import ca.bc.gov.data.local.entity.relations.PatientWithMedicationRecords
import ca.bc.gov.data.local.entity.relations.PatientWithTestResultsAndRecords
import ca.bc.gov.data.local.entity.relations.PatientWithVaccineAndDoses
import ca.bc.gov.data.local.entity.relations.TestResultWithRecord
import ca.bc.gov.data.local.entity.relations.TestResultWithRecordsAndPatient
import ca.bc.gov.data.local.entity.relations.VaccineRecordWithDose

fun PatientEntity.toDto() = PatientDto(
    id, fullName, dateOfBirth, phn, authenticationStatus = authenticationStatus
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
    resultDescription.split("|"),
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
    testResult = testResult.toDto(),
    testRecords = testRecords.map { it.toDto() }
)

fun VaccineRecordWithDose.toDto() = VaccineWithDosesDto(
    vaccineRecordEntity.toDto(),
    doses = vaccineDoses.map { it.toDto() }
)

fun PatientWithVaccineAndDoses.toDto() = PatientWithVaccineAndDosesDto(
    patient = patient.toDto(),
    vaccineWithDoses = vaccineRecordWithDose?.toDto()
)

fun PatientWithTestResultsAndRecords.toDto() = PatientWithTestResultsAndRecordsDto(
    patient = patient.toDto(),
    testResultWithRecords = testResultsWithRecords.map { it.toDto() }
)

fun TestResultWithRecordsAndPatient.toDto() = TestResultWithRecordsAndPatientDto(
    testResultWithRecords = testResultWithRecord.toDto(),
    patient = patient.toDto()
)

fun MedicationRecordEntity.toDto() = MedicationRecordDto(
    id,
    patientId,
    practitionerIdentifier,
    prescriptionStatus,
    practitionerSurname,
    dispenseDate,
    directions,
    dateEntered,
    dataSource
)

fun PatientWithMedicationRecords.toDto() = PatientWithMedicationRecordDto(
    patient.toDto(),
    medicationRecord = medicationRecord.map { it.toDto() }
)
