package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.model.immunization.ImmunizationForecastDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastAndPatientDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastDto
import ca.bc.gov.common.model.labtest.LabOrderDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestsAndPatientDto
import ca.bc.gov.common.model.labtest.LabTestDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.patient.PatientListDto
import ca.bc.gov.common.model.patient.PatientWithCovidOrderAndTestDto
import ca.bc.gov.common.model.patient.PatientWithImmunizationRecordAndForecastDto
import ca.bc.gov.common.model.patient.PatientWithLabOrderAndLatTestsDto
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.common.model.relation.PatientWithMedicationRecordDto
import ca.bc.gov.common.model.relation.PatientWithTestResultsAndRecordsDto
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsAndPatientDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsDto
import ca.bc.gov.common.model.relation.VaccineWithDosesDto
import ca.bc.gov.common.model.test.CovidOrderDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestAndPatientDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto
import ca.bc.gov.common.utils.titleCase
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.comment.CommentEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderWithCovidTests
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderWithCovidTestsAndPatient
import ca.bc.gov.data.datasource.local.entity.covid.CovidTestEntity
import ca.bc.gov.data.datasource.local.entity.covid.test.TestRecordEntity
import ca.bc.gov.data.datasource.local.entity.covid.test.TestResultEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineDoseEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineRecordEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationForecastEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecordEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecordWithForecast
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecordWithForecastAndPatient
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderWithLabTests
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderWithLabTestsAndPatient
import ca.bc.gov.data.datasource.local.entity.labtest.LabTestEntity
import ca.bc.gov.data.datasource.local.entity.medication.DispensingPharmacyEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationRecordEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationSummaryEntity
import ca.bc.gov.data.datasource.local.entity.relations.MedicationWithSummaryAndPharmacy
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithCovidOrderAndCovidTest
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithImmunizationRecordAndForecast
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithLabOrdersAndLabTests
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithMedicationRecords
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithTestResultsAndRecords
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithVaccineAndDoses
import ca.bc.gov.data.datasource.local.entity.relations.TestResultWithRecord
import ca.bc.gov.data.datasource.local.entity.relations.TestResultWithRecordsAndPatient
import ca.bc.gov.data.datasource.local.entity.relations.VaccineRecordWithDose
import java.time.Instant

fun PatientEntity.toDto() = PatientDto(
    id, fullName.titleCase(), dateOfBirth, phn, authenticationStatus = authenticationStatus
)

fun TestResultEntity.toDto() = TestResultDto(
    id, patientId, collectionDate, dataSource
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
    prescriptionIdentifier,
    prescriptionStatus,
    practitionerSurname,
    dispenseDate,
    directions,
    dateEntered,
    dataSource
)

fun MedicationSummaryEntity.toDto() = MedicationSummaryDto(
    id,
    medicationRecordId,
    din,
    brandName,
    genericName,
    quantity,
    maxDailyDosage,
    drugDiscontinueDate ?: Instant.EPOCH,
    form,
    manufacturer,
    strength,
    strengthUnit,
    isPin ?: false
)

fun DispensingPharmacyEntity.toDto() = DispensingPharmacyDto(
    id,
    medicationRecordId,
    pharmacyId,
    name,
    addressLine1,
    addressLine2,
    city, province, postalCode, countryCode, phoneNumber, faxNumber
)

fun MedicationWithSummaryAndPharmacy.toDto() = MedicationWithSummaryAndPharmacyDto(
    medicationRecord = medicationRecord.toDto(),
    medicationSummary = medicationSummary.toDto(),
    dispensingPharmacy = dispensingPharmacy.toDto()
)

fun PatientWithMedicationRecords.toDto() = PatientWithMedicationRecordDto(
    patient.toDto(),
    medicationRecord = medicationRecord.map { it.toDto() }
)

fun PatientWithLabOrdersAndLabTests.toDto() = PatientWithLabOrderAndLatTestsDto(
    patient = patient.toDto(),
    labOrdersWithLabTests = labOrdersWithLabTests.map { it.toDto() }
)

fun PatientWithCovidOrderAndCovidTest.toDto() = PatientWithCovidOrderAndTestDto(
    patient = patient.toDto(),
    covidOrderAndTests = covidOrderWithTests.map { it.toDto() }
)

fun PatientWithImmunizationRecordAndForecast.toDto() = PatientWithImmunizationRecordAndForecastDto(
    patient = patient.toDto(),
    immunizationRecords = immunizationRecords.map { it.toDto() }
)

fun List<PatientEntity>.toDto() = PatientListDto(
    patientDtos = this.map { it.toDto() }
)

fun LabOrderEntity.toDto() = LabOrderDto(
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
    reportAvailable
)

fun LabTestEntity.toDto() = LabTestDto(
    id, labOrderId, obxId, batteryType, outOfRange, loinc, testStatus
)

fun LabOrderWithLabTests.toDto() = LabOrderWithLabTestDto(
    labOrder.toDto(),
    labTests.map { it.toDto() }
)

fun LabOrderWithLabTestsAndPatient.toDto() = LabOrderWithLabTestsAndPatientDto(
    labOrderWithLabTest = labOrderWithLabTests.toDto(),
    patient = patient.toDto()
)

fun CommentEntity.toDto() = CommentDto(
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

fun CovidOrderEntity.toDto() = CovidOrderDto(
    id,
    phn,
    orderingProviderIds,
    orderingProviders,
    reportingLab,
    location,
    ormOrOru,
    messageDateTime,
    messageId,
    additionalData,
    reportAvailable,
    patientId,
    dataSource = dataSource
)

fun CovidTestEntity.toDto() = CovidTestDto(
    id,
    testType,
    outOfRange,
    collectedDateTime,
    testStatus,
    labResultOutcome,
    resultDescription?.split("|") ?: emptyList(),
    resultLink,
    receivedDateTime,
    resultDateTime,
    loinc,
    loincName,
    covidOrderId
)

fun CovidOrderWithCovidTests.toDto() = CovidOrderWithCovidTestDto(
    covidOrder.toDto(),
    covidTests.map { it.toDto() }
)

fun CovidOrderWithCovidTestsAndPatient.toDto() = CovidOrderWithCovidTestAndPatientDto(
    covidOrderWithCovidTests.toDto(),
    patient.toDto()
)

fun ImmunizationRecordEntity.toDto() = ImmunizationRecordDto(
    id,
    patientId,
    immunizationId,
    dateOfImmunization,
    status,
    isValid,
    providerOrClinic,
    targetedDisease,
    immunizationName,
    agentCode,
    agentName,
    lotNumber,
    productName
)

fun ImmunizationForecastEntity.toDto() = ImmunizationForecastDto(
    id,
    immunizationRecordId,
    recommendationId,
    createDate,
    status,
    displayName,
    eligibleDate,
    dueDate
)

fun ImmunizationRecordWithForecast.toDto() = ImmunizationRecordWithForecastDto(
    immunizationRecord = immunizationRecord.toDto(),
    immunizationForecast = immunizationForecast?.toDto()
)

fun ImmunizationRecordWithForecastAndPatient.toDto() = ImmunizationRecordWithForecastAndPatientDto(
    immunizationRecordWithForecast = immunizationRecordWithForecast.toDto(),
    patient = patient.toDto()
)
