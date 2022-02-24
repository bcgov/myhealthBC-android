package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.utils.formatInPattern
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toDateTime
import ca.bc.gov.data.model.MediaMetaData
import ca.bc.gov.data.model.VaccineStatus
import ca.bc.gov.data.remote.model.base.LabResult
import ca.bc.gov.data.remote.model.base.covidtest.CovidTestRecord
import ca.bc.gov.data.remote.model.base.medication.DispensingPharmacy
import ca.bc.gov.data.remote.model.base.medication.MedicationStatementPayload
import ca.bc.gov.data.remote.model.base.medication.MedicationSummary
import ca.bc.gov.data.remote.model.base.vaccine.Media
import ca.bc.gov.data.remote.model.base.vaccine.VaccineResourcePayload
import java.time.Instant
import java.time.format.DateTimeFormatter

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
    resultDateTime = resultDateTime.toDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    testName = loIncName ?: "",
    testOutcome = labResultOutcome ?: "",
    testType = testType,
    testStatus = testStatus ?: "",
    resultTitle = "",
    resultDescription = resultDescription,
    resultLink = resultLink
)

fun MedicationStatementPayload.toMedicationRecordDto(patientId: Long) = MedicationRecordDto(
    id = 0,
    patientId = patientId,
    practitionerIdentifier = prescriptionIdentifier,
    prescriptionStatus = prescriptionStatus.toString(),
    practitionerSurname = practitionerSurname,
    dispenseDate = dispensedDate.toDateTime(),
    directions = directions,
    dateEntered = dateEntered?.toDateTime() ?: Instant.EPOCH,
    dataSource = DataSource.BCSC
)

fun MedicationSummary.toMedicationSummaryDto(medicationRecordId: Long) = MedicationSummaryDto(
    id = 0,
    medicationRecordId = medicationRecordId,
    din = din,
    brandName = brandName,
    genericName = genericName,
    quantity = quantity,
    maxDailyDosage = maxDailyDosage,
    drugDiscontinueDate = drugDiscontinuedDate?.toDateTime() ?: Instant.EPOCH,
    form = form,
    manufacturer = manufacturer,
    strength = strength,
    strengthUnit = strengthUnit,
    isPin = isPin ?: false
)

fun DispensingPharmacy.toDispensingPharmacyDto(medicationRecordId: Long) = DispensingPharmacyDto(
    id = 0,
    medicationRecordId = medicationRecordId,
    pharmacyId = pharmacyId,
    name = name,
    addressLine1 = addressLine1,
    addressLine2 = addressLine2,
    city = city,
    province = province,
    postalCode = postalCode,
    countryCode = countryCode,
    phoneNumber = phoneNumber,
    faxNumber = faxNumber
)

fun Media.toMediaMetaData(): MediaMetaData = MediaMetaData(
    mediaType = mediaType ?: throw MyHealthException(SERVER_ERROR, "Invalid Response"),
    encoding = encoding ?: throw MyHealthException(SERVER_ERROR, "Invalid Response"),
    data = data ?: throw MyHealthException(SERVER_ERROR, "Invalid Response"),
)

fun VaccineResourcePayload.toVaccineStatus(): VaccineStatus = VaccineStatus(
    phn = phn,
    qrCode = qrCode.toMediaMetaData(),
    federalVaccineProof = federalVaccineProof.toMediaMetaData()
)
