package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.model.immunization.ImmunizationForecastDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordDto
import ca.bc.gov.common.model.labtest.LabOrderDto
import ca.bc.gov.common.model.labtest.LabTestDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.CovidOrderDto
import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.comment.CommentEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidTestEntity
import ca.bc.gov.data.datasource.local.entity.covid.test.TestRecordEntity
import ca.bc.gov.data.datasource.local.entity.covid.test.TestResultEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineDoseEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineRecordEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationForecastEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecordEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabTestEntity
import ca.bc.gov.data.datasource.local.entity.medication.DispensingPharmacyEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationRecordEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationSummaryEntity

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
    prescriptionIdentifier,
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

fun LabOrderDto.toEntity() = LabOrderEntity(
    id,
    patientId,
    labPdfId,
    reportId,
    collectionDateTime,
    timelineDateTime,
    reportingSource,
    commonName,
    orderingProvider,
    testStatus,
    reportingAvailable
)

fun LabTestDto.toEntity() = LabTestEntity(
    id, labOrderId, obxId, batteryType, outOfRange, loinc, testStatus
)

fun CommentDto.toEntity() = CommentEntity(
    id,
    userProfileId,
    text,
    entryTypeCode,
    parentEntryId,
    version,
    createdDateTime,
    createdBy,
    updatedDateTime,
    updatedBy
)

fun CovidOrderDto.toEntity() = CovidOrderEntity(
    id,
    patientId,
    phn,
    orderingProviderIds,
    orderingProviders,
    reportingLab,
    location,
    ormOrOru,
    messageDateTime,
    messageId,
    additionalData,
    reportAvailable
)

fun CovidTestDto.toEntity() = CovidTestEntity(
    id,
    covidOrderId,
    testType,
    outOfRange,
    collectedDateTime,
    testStatus,
    labResultOutcome,
    resultDescription = resultDescription.joinToString("|"),
    resultLink,
    receivedDateTime,
    resultDateTime,
    loInc,
    loIncName
)

fun ImmunizationRecordDto.toEntity() = ImmunizationRecordEntity(
    id, patientId, immunizationId, dateOfImmunization, status, isValid,
    provideOrClinic, targetedDisease, immunizationName, agentCode, agentName, lotNumber, productName
)

fun ImmunizationForecastDto.toEntity() = ImmunizationForecastEntity(
    id,
    immunizationRecordId,
    recommendationId,
    createDate,
    status,
    displayName,
    eligibleDate,
    dueDate
)
