package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.PatientAddressDto
import ca.bc.gov.common.model.TermsOfServiceDto
import ca.bc.gov.common.model.banner.BannerDto
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.model.healthvisits.ClinicDto
import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.common.model.immunization.ForecastStatus
import ca.bc.gov.common.model.immunization.ImmunizationDto
import ca.bc.gov.common.model.immunization.ImmunizationForecastDto
import ca.bc.gov.common.model.immunization.ImmunizationRecommendationsDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastDto
import ca.bc.gov.common.model.labtest.LabOrderDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.labtest.LabTestDto
import ca.bc.gov.common.model.notification.NotificationActionTypeDto
import ca.bc.gov.common.model.notification.NotificationDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.patient.PatientNameDto
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.common.model.services.BcCancerScreeningDataDto
import ca.bc.gov.common.model.services.DiagnosticImagingDataDto
import ca.bc.gov.common.model.services.OrganDonorDto
import ca.bc.gov.common.model.services.OrganDonorStatusDto
import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.common.model.test.CovidOrderDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.common.model.userprofile.UserProfileDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toDateTime
import ca.bc.gov.common.utils.toDateTimeZ
import ca.bc.gov.common.utils.toOffsetDateTime
import ca.bc.gov.common.utils.toPstFromIsoZoned
import ca.bc.gov.data.datasource.remote.model.base.CovidLabResult
import ca.bc.gov.data.datasource.remote.model.base.CovidOrder
import ca.bc.gov.data.datasource.remote.model.base.TermsOfServicePayload
import ca.bc.gov.data.datasource.remote.model.base.banner.BannerPayload
import ca.bc.gov.data.datasource.remote.model.base.clinicaldocument.ClinicalDocumentResponse
import ca.bc.gov.data.datasource.remote.model.base.comment.CommentPayload
import ca.bc.gov.data.datasource.remote.model.base.dependent.DependentPayload
import ca.bc.gov.data.datasource.remote.model.base.healthvisits.Clinic
import ca.bc.gov.data.datasource.remote.model.base.healthvisits.HealthVisitsPayload
import ca.bc.gov.data.datasource.remote.model.base.healthvisits.HealthVisitsResponse
import ca.bc.gov.data.datasource.remote.model.base.hospitalvisit.HospitalVisitInformation
import ca.bc.gov.data.datasource.remote.model.base.hospitalvisit.HospitalVisitPayload
import ca.bc.gov.data.datasource.remote.model.base.immunization.Forecast
import ca.bc.gov.data.datasource.remote.model.base.immunization.ImmunizationRecord
import ca.bc.gov.data.datasource.remote.model.base.immunization.Recommendation
import ca.bc.gov.data.datasource.remote.model.base.medication.DispensingPharmacy
import ca.bc.gov.data.datasource.remote.model.base.medication.MedicationStatementPayload
import ca.bc.gov.data.datasource.remote.model.base.medication.MedicationSummary
import ca.bc.gov.data.datasource.remote.model.base.patient.PatientName
import ca.bc.gov.data.datasource.remote.model.base.patientdata.BcCancerScreeningData
import ca.bc.gov.data.datasource.remote.model.base.patientdata.DiagnosticImagingData
import ca.bc.gov.data.datasource.remote.model.base.patientdata.OrganDonorData
import ca.bc.gov.data.datasource.remote.model.base.patientdata.PatientDataType
import ca.bc.gov.data.datasource.remote.model.base.patientdata.organdonor.OrganDonorStatus
import ca.bc.gov.data.datasource.remote.model.base.profile.UserProfilePayload
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
import ca.bc.gov.data.datasource.remote.model.response.MedicationStatementResponse
import ca.bc.gov.data.datasource.remote.model.response.NotificationResponse
import ca.bc.gov.data.datasource.remote.model.response.PatientAddress
import ca.bc.gov.data.datasource.remote.model.response.PatientDataResponse
import ca.bc.gov.data.datasource.remote.model.response.PatientResponse
import ca.bc.gov.data.model.MediaMetaData
import ca.bc.gov.data.model.VaccineStatus
import java.time.Instant
import java.time.format.DateTimeFormatter

fun PatientAddress.toDto() = PatientAddressDto(
    streetLines = streetLines,
    city = city,
    province = province,
    postalCode = postalCode,
)

fun UserProfilePayload.toDto(patientId: Long) = UserProfileDto(
    patientId = patientId,
    acceptedTermsOfService = acceptedTermsOfService,
    email = email,
    isEmailVerified = isEmailVerified,
    smsNumber = smsNumber,
    isPhoneVerified = isSMSNumberVerified,
    hasTermsOfServiceUpdated = hasTermsOfServiceUpdated
)

fun MedicationStatementResponse.toListOfMedicationDto(): List<MedicationWithSummaryAndPharmacyDto> =
    this.payload?.mapNotNull {
        val summaryDto = it.medicationSummary?.toMedicationSummaryDto()
        val dispensingPharmacyDto = it.dispensingPharmacy?.toDispensingPharmacyDto()

        if (summaryDto == null || dispensingPharmacyDto == null) {
            null
        } else {
            MedicationWithSummaryAndPharmacyDto(
                it.toMedicationRecordDto(),
                summaryDto,
                dispensingPharmacyDto
            )
        }
    } ?: listOf()

fun MedicationStatementPayload.toMedicationRecordDto() = MedicationRecordDto(
    prescriptionIdentifier = prescriptionIdentifier,
    prescriptionStatus = prescriptionStatus.toString(),
    practitionerSurname = practitionerSurname,
    dispenseDate = dispensedDate.toDateTime(),
    directions = directions,
    dateEntered = dateEntered?.toDateTime() ?: Instant.EPOCH,
    dataSource = DataSource.BCSC
)

fun MedicationSummary.toMedicationSummaryDto() = MedicationSummaryDto(
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

fun DispensingPharmacy.toDispensingPharmacyDto() = DispensingPharmacyDto(
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
    qrCode = qrCode?.toMediaMetaData(),
    federalVaccineProof = federalVaccineProof?.toMediaMetaData()
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

fun CovidOrder.toDto() = CovidOrderDto(
    covidOrderId = id,
    phn = phn,
    orderingProviderIds = orderingProviderIds,
    orderingProviders = orderingProviders,
    reportingLab = reportingLab,
    location = location,
    ormOrOru = ormOrOru,
    messageDateTime = messageDateTime.toDateTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    messageId = messageId,
    additionalData = additionalData,
    reportAvailable = reportAvailable
)

fun CovidLabResult.toDto() = CovidTestDto(
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

fun HospitalVisitPayload?.toDto(): List<HospitalVisitDto> {
    if (this == null) return emptyList()
    return this.list.map { it.toDto() }
}

fun HospitalVisitInformation.toDto() = HospitalVisitDto(
    healthService = healthService.orEmpty(),
    location = facility,
    provider = provider.orEmpty(),
    visitType = visitType.orEmpty(),
    visitDate = admitDateTime.toDateTime(),
    dischargeDate = endDateTime?.toDateTime(),
    encounterId = encounterId
)

fun ClinicalDocumentResponse.toDto(): List<ClinicalDocumentDto> =
    this.payload.map {
        ClinicalDocumentDto(
            name = it.name,
            type = it.type,
            facilityName = it.facilityName,
            discipline = it.discipline,
            serviceDate = it.serviceDate.toOffsetDateTime(),
            fileId = it.fileId,
        )
    }

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

fun BannerPayload.toDto(): BannerDto? {
    val startDate = this.startDate.toDateTimeZ()
    val endDate = this.endDate.toDateTimeZ()
    val currentTime = System.currentTimeMillis()
    return if (startDate.toEpochMilli() <= currentTime &&
        currentTime < endDate.toEpochMilli()
    ) {
        BannerDto(
            title = this.title,
            body = this.body,
            startDate = this.startDate.toDateTimeZ(),
            endDate = this.endDate.toDateTimeZ()
        )
    } else {
        null
    }
}

fun DependentPayload.toDto() = DependentDto(
    hdid = dependentInformation.hdid,
    firstname = dependentInformation.firstname,
    lastname = dependentInformation.lastname,
    phn = dependentInformation.phn,
    dateOfBirth = dependentInformation.dateOfBirth.toDate(),
    gender = dependentInformation.gender,
    ownerId = ownerId,
    delegateId = delegateId,
    reasonCode = reasonCode,
    totalDelegateCount = totalDelegateCount,
    version = version
)

fun PatientName.toDto(): PatientNameDto {
    return PatientNameDto(
        givenName,
        surName
    )
}

fun PatientResponse.toDto(): PatientDto {

    val patientName = preferredName ?: throw MyHealthException(
        SERVER_ERROR, INVALID_RESPONSE
    )
    val fullNameBuilder = StringBuilder()
    fullNameBuilder.append(
        "${
        patientName.givenName ?: throw MyHealthException(
            SERVER_ERROR, INVALID_RESPONSE
        )
        } "
    )
    fullNameBuilder.append(
        patientName.surName ?: throw MyHealthException(
            SERVER_ERROR, INVALID_RESPONSE
        )
    )
    val date = birthDate ?: throw MyHealthException(SERVER_ERROR, INVALID_RESPONSE)

    return PatientDto(
        id = 0,
        hdid = hdid ?: throw MyHealthException(SERVER_ERROR, INVALID_RESPONSE),
        phn = personalHealthNumber ?: throw MyHealthException(SERVER_ERROR, INVALID_RESPONSE),
        fullName = fullNameBuilder.toString(),
        firstName = patientName.givenName,
        lastName = patientName.surName,
        dateOfBirth = date.toDate(),
        legalName = legalName?.toDto(),
        preferredName = preferredName.toDto(),
        commonName = commonName?.toDto(),
        authenticationStatus = AuthenticationStatus.AUTHENTICATED,
        physicalAddress = physicalAddress?.toDto(),
        mailingAddress = mailingAddress?.toDto()
    )
}

private fun OrganDonorStatus.toDto() = when (this) {
    OrganDonorStatus.UNKNOWN -> OrganDonorStatusDto.UNKNOWN
    OrganDonorStatus.ERROR -> OrganDonorStatusDto.ERROR
    OrganDonorStatus.PENDING -> OrganDonorStatusDto.PENDING
    OrganDonorStatus.NOT_REGISTERED -> OrganDonorStatusDto.NOT_REGISTERED
    OrganDonorStatus.REGISTERED -> OrganDonorStatusDto.REGISTERED
}

private fun OrganDonorData.toDto() = OrganDonorDto(
    id = 0,
    patientId = 0,
    status = status.toDto(),
    statusMessage = statusMessage,
    registrationFileId = registrationFileId,
    file = null
)

private fun DiagnosticImagingData.toDto() = DiagnosticImagingDataDto(
    id = id,
    examDate = examDate?.toDateTime(),
    isUpdated = isUpdated,
    fileId = fileId,
    examStatus = examStatus ?: "Unknown",
    healthAuthority = healthAuthority,
    organization = organization,
    modality = modality,
    bodyPart = bodyPart,
    procedureDescription = procedureDescription
)

private fun BcCancerScreeningData.toDto() = BcCancerScreeningDataDto(
    id = id,
    resultDateTime = resultDateTime?.toDateTimeZ(),
    eventDateTime = eventDateTime?.toDateTimeZ(),
    fileId = fileId,
    programName = programName,
    eventType = eventType
)

fun PatientDataResponse.toDto() = items.map { data ->
    when (data.type) {
        PatientDataType.ORGAN_DONOR_REGISTRATION -> {
            (data as OrganDonorData).toDto()
        }

        PatientDataType.DIAGNOSTIC_IMAGING_EXAM -> {
            (data as DiagnosticImagingData).toDto()
        }

        PatientDataType.BC_CANCER_SCREENING -> {
            (data as BcCancerScreeningData).toDto()
        }
    }
}

fun NotificationResponse.toDto(hdid: String) = NotificationDto(
    notificationId = id,
    hdid = hdid,
    category = category,
    displayText = displayText,
    actionUrl = actionUrl,
    actionType = NotificationActionTypeDto.getByValue(actionType),
    date = date.toPstFromIsoZoned()
)
