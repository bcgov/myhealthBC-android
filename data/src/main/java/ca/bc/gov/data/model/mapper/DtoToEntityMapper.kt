package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto
import ca.bc.gov.data.local.entity.DispensingPharmacyEntity
import ca.bc.gov.data.local.entity.MedicationRecordEntity
import ca.bc.gov.data.local.entity.MedicationSummaryEntity
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.TestRecordEntity
import ca.bc.gov.data.local.entity.TestResultEntity
import ca.bc.gov.data.local.entity.VaccineDoseEntity
import ca.bc.gov.data.local.entity.VaccineRecordEntity

fun PatientDto.toEntity() = PatientEntity(
    id,
    fullName,
    dateOfBirth = dateOfBirth,
    phn = phn,
    patientOrder = Long.MAX_VALUE,
    authenticationStatus = authenticationStatus
)

fun VaccineDoseDto.toEntity() = VaccineDoseEntity(
    vaccineRecordId = vaccineRecordId,
    productName = productName,
    providerName = providerName,
    lotNumber = lotNumber,
    date = date
)

fun TestResultDto.toEntity() = TestResultEntity(
    id,
    patientId,
    collectionDate,
    dataSource
)

fun VaccineRecordDto.toEntity() = VaccineRecordEntity(
    id,
    patientId,
    qrIssueDate,
    status,
    shcUri,
    federalPass,
    mode
)

fun TestRecordDto.toEntity() = TestRecordEntity(
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
    resultDescription = resultDescription.joinToString("|")
)

fun MedicationRecordDto.toEntity() = MedicationRecordEntity(
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

fun MedicationSummaryDto.toEntity() = MedicationSummaryEntity(
    id,
    medicationRecordId,
    din,
    brandName,
    genericName,
    quantity,
    maxDailyDosage,
    drugDiscontinueDate,
    form,
    manufacturer,
    strength,
    strengthUnit,
    isPin
)

fun DispensingPharmacyDto.toEntity() = DispensingPharmacyEntity(
    id,
    medicationRecordId,
    pharmacyId,
    name,
    addressLine1,
    addressLine2,
    city,
    province,
    postalCode,
    countryCode,
    phoneNumber,
    faxNumber
)
