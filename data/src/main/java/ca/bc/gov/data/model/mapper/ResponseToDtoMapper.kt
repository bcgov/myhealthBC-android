package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.TermsOfServiceDto
import ca.bc.gov.common.model.banner.BannerDto
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.model.healthvisits.ClinicDto
import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.common.model.immunization.ForecastStatus
import ca.bc.gov.common.model.immunization.ImmunizationDto
import ca.bc.gov.common.model.immunization.ImmunizationForecastDto
import ca.bc.gov.common.model.immunization.ImmunizationRecommendationsDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastDto
import ca.bc.gov.common.model.labtest.LabOrderDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.labtest.LabTestDto
import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.common.model.test.CovidOrderDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toDateTime
import ca.bc.gov.common.utils.toDateTimeZ
import ca.bc.gov.data.datasource.remote.model.base.LabResult
import ca.bc.gov.data.datasource.remote.model.base.Order
import ca.bc.gov.data.datasource.remote.model.base.TermsOfServicePayload
import ca.bc.gov.data.datasource.remote.model.base.banner.BannerPayload
import ca.bc.gov.data.datasource.remote.model.base.comment.CommentPayload
import ca.bc.gov.data.datasource.remote.model.base.covidtest.CovidTestRecord
import ca.bc.gov.data.datasource.remote.model.base.dependent.DependentInformation
import ca.bc.gov.data.datasource.remote.model.base.healthvisits.Clinic
import ca.bc.gov.data.datasource.remote.model.base.healthvisits.HealthVisitsPayload
import ca.bc.gov.data.datasource.remote.model.base.healthvisits.HealthVisitsResponse
import ca.bc.gov.data.datasource.remote.model.base.immunization.Forecast
import ca.bc.gov.data.datasource.remote.model.base.immunization.ImmunizationRecord
import ca.bc.gov.data.datasource.remote.model.base.immunization.Recommendation
import ca.bc.gov.data.datasource.remote.model.base.medication.DispensingPharmacy
import ca.bc.gov.data.datasource.remote.model.base.medication.MedicationStatementPayload
import ca.bc.gov.data.datasource.remote.model.base.medication.MedicationSummary
import ca.bc.gov.data.datasource.remote.model.base.specialauthority.SpecialAuthorityPayload
import ca.bc.gov.data.datasource.remote.model.base.specialauthority.SpecialAuthorityResponse
import ca.bc.gov.data.datasource.remote.model.base.vaccine.Media
import ca.bc.gov.data.datasource.remote.model.base.vaccine.VaccineResourcePayload
import ca.bc.gov.data.datasource.remote.model.response.AddCommentResponse
import ca.bc.gov.data.datasource.remote.model.response.AllCommentsResponse
import ca.bc.gov.data.datasource.remote.model.response.AuthenticatedCovidTestResponse
import ca.bc.gov.data.datasource.remote.model.response.CommentResponse
import ca.bc.gov.data.datasource.remote.model.response.ImmunizationResponse
import ca.bc.gov.data.datasource.remote.model.response.LabTestResponse
import ca.bc.gov.data.model.MediaMetaData
import ca.bc.gov.data.model.VaccineStatus
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

fun MedicationStatementPayload.toMedicationRecordDto(patientId: Long) = MedicationRecordDto(
    id = 0,
    patientId = patientId,
    prescriptionIdentifier = prescriptionIdentifier,
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

const val INVALID_RESPONSE = "Invalid Response"
fun Media.toMediaMetaData(): MediaMetaData = MediaMetaData(
    mediaType = mediaType ?: throw MyHealthException(SERVER_ERROR, INVALID_RESPONSE),
    encoding = encoding ?: throw MyHealthException(SERVER_ERROR, INVALID_RESPONSE),
    data = data ?: throw MyHealthException(SERVER_ERROR, INVALID_RESPONSE),
)

fun VaccineResourcePayload.toVaccineStatus(): VaccineStatus = VaccineStatus(
    phn = phn,
    qrCode = qrCode.toMediaMetaData(),
    federalVaccineProof = federalVaccineProof.toMediaMetaData()
)

fun LabTestResponse.toDto(): List<LabOrderWithLabTestDto> {
    return payload.orders.map { order ->
        val tests = order.laboratoryTests.map { test ->
            LabTestDto(
                obxId = test.obxId,
                batteryType = test.batteryType,
                outOfRange = test.outOfRange,
                loinc = test.loinc,
                testStatus = test.testStatus
            )
        }
        LabOrderWithLabTestDto(
            LabOrderDto(
                labPdfId = order.labPdfId,
                reportId = order.reportId,
                collectionDateTime = order.collectionDateTime?.toDateTime(),
                timelineDateTime = order.timelineDateTime.toDateTime(),
                reportingSource = order.reportingSource,
                commonName = order.commonName,
                orderingProvider = order.orderingProvider,
                testStatus = order.testStatus,
                orderStatus = order.orderStatus,
                reportingAvailable = order.reportAvailable
            ),
            tests
        )
    }
}

fun CommentPayload.toDto() = CommentDto(
    id,
    userProfileId,
    text,
    entryTypeCode,
    parentEntryId,
    version,
    createdDateTime = createdDateTime.toDateTime(DateTimeFormatter.ISO_ZONED_DATE_TIME),
    createdBy,
    updatedDateTime = updatedDateTime.toDateTime(DateTimeFormatter.ISO_ZONED_DATE_TIME),
    updatedBy
)

fun CommentPayload.toAddCommentDto() = CommentDto(
    id,
    userProfileId,
    text,
    entryTypeCode,
    parentEntryId,
    version,
    createdDateTime = createdDateTime.toDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    createdBy,
    updatedDateTime = updatedDateTime.toDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    updatedBy
)

fun CommentResponse.toDto(): List<CommentDto> {
    return payload.map { it.toDto() }
}

fun AddCommentResponse.toDto(): CommentDto {
    return payload.toAddCommentDto()
}

fun AllCommentsResponse.toDto(): List<CommentDto> {
    val listCommentDto: MutableList<CommentDto> = mutableListOf()
    payload.values.forEach { commentPayloadList ->
        listCommentDto.addAll(commentPayloadList.map { it.toDto() })
    }
    return listCommentDto
}

fun Order.toDto() = CovidOrderDto(
    id,
    phn,
    orderingProviderIds,
    orderingProviders,
    reportingLab,
    location,
    ormOrOru,
    messageDateTime.toDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    messageId,
    additionalData, reportAvailable
)

fun LabResult.toDto() = CovidTestDto(
    id,
    testType,
    outOfRange,
    collectedDateTime.toDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    testStatus,
    labResultOutcome,
    resultDescription,
    resultLink,
    receivedDateTime.toDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    resultDateTime.toDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    loInc,
    loIncName
)

fun AuthenticatedCovidTestResponse.toDto(): List<CovidOrderWithCovidTestDto> {
    return payload.orders.map { order ->
        val covidOrder = order.toDto()
        val covidTest = order.labResults.map { it.toDto() }
        covidTest.forEach { it.covidOrderId = covidOrder.id }
        CovidOrderWithCovidTestDto(covidOrder, covidTest)
    }
}

fun TermsOfServicePayload.toDto() = TermsOfServiceDto(
    id,
    content,
    effectiveDate
)

fun ImmunizationRecord.toDto(): ImmunizationRecordDto {

    val agent = immunization.immunizationAgents.firstOrNull()

    return ImmunizationRecordDto(
        immunizationId = id,
        dateOfImmunization = dateOfImmunization.toDateTime(),
        status = status,
        isValid = valid,
        provideOrClinic = providerOrClinic,
        targetedDisease = targetedDisease,
        immunizationName = immunization.name,
        agentName = agent?.name,
        agentCode = agent?.code,
        lotNumber = agent?.lotNumber,
        productName = agent?.productName
    )
}

fun Forecast.toDto() = ImmunizationForecastDto(
    recommendationId = recommendationId,
    createDate = createDate.toDateTime(),
    status = ForecastStatus.getByText(status),
    displayName = displayName,
    eligibleDate = eligibleDate.toDateTime(),
    dueDate = dueDate.toDateTime()
)

fun ImmunizationResponse.toDto() = ImmunizationDto(
    records = this.payload.immunizations.map {
        ImmunizationRecordWithForecastDto(
            it.toDto(),
            it.forecast?.toDto()
        )
    },
    recommendations = this.payload.recommendations.mapNotNull {
        if (it.recommendedVaccinations.isNullOrBlank()) {
            null
        } else {
            it.toDto()
        }
    }
)

fun HealthVisitsResponse.toDto(): List<HealthVisitsDto> {
    return payload.map { it.toDto() }
}

fun HealthVisitsPayload.toDto() = HealthVisitsDto(
    healthVisitId = 0,
    patientId = 0,
    id,
    encounterDate.toDateTime(),
    specialtyDescription,
    practitionerName,
    clinic.toDto(),
    dataSource = DataSource.BCSC
)

fun Clinic.toDto() = ClinicDto(
    name
)

fun SpecialAuthorityResponse.toDto(): List<SpecialAuthorityDto> {
    return payload.map { it.toDto() }
}

fun SpecialAuthorityPayload.toDto() = SpecialAuthorityDto(
    specialAuthorityId = 0,
    patientId = 0,
    referenceNumber,
    drugName,
    requestStatus,
    prescriberFirstName,
    prescriberLastName,
    requestedDate?.toDateTime(),
    effectiveDate?.toDateTime(),
    expiryDate?.toDateTime(),
    dataSource = DataSource.BCSC
)

fun Recommendation.toDto(): ImmunizationRecommendationsDto {
    val agent = immunization.immunizationAgents.firstOrNull()

    return ImmunizationRecommendationsDto(
        recommendationSetId = this.recommendationSetId,
        immunizationName = agent?.name,
        status = ForecastStatus.getByText(status),
        agentDueDate = this.agentDueDate?.toDateTime(),
        recommendedVaccinations = this.recommendedVaccinations
    )
}

fun BannerPayload.toDto() = BannerDto(
    title = this.title,
    body = this.body,
    startDate = this.startDate.toDateTimeZ(),
    endDate = this.endDate.toDateTimeZ()
)

fun DependentInformation.toDto() = DependentDto(
    hdid = hdid,
    firstname = firstname,
    lastname = lastname,
    PHN = PHN,
    dateOfBirth = dateOfBirth.toDateTime(),
    gender = gender,
)