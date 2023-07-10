package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.PatientAddressDto
import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.common.model.immunization.ImmunizationForecastDto
import ca.bc.gov.common.model.immunization.ImmunizationRecommendationsDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordDto
import ca.bc.gov.common.model.labtest.LabOrderDto
import ca.bc.gov.common.model.labtest.LabTestDto
import ca.bc.gov.common.model.notification.NotificationDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.patient.PatientNameDto
import ca.bc.gov.common.model.services.DiagnosticImagingDataDto
import ca.bc.gov.common.model.services.OrganDonorDto
import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.common.model.settings.QuickAccessTileDto
import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.common.model.test.CovidOrderDto
import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.common.model.userprofile.UserProfileDto
import ca.bc.gov.data.datasource.local.entity.PatientAddressEntity
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.PatientNameEntity
import ca.bc.gov.data.datasource.local.entity.clinicaldocument.ClinicalDocumentEntity
import ca.bc.gov.data.datasource.local.entity.comment.CommentEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidTestEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineDoseEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineRecordEntity
import ca.bc.gov.data.datasource.local.entity.dependent.DependentEntity
import ca.bc.gov.data.datasource.local.entity.healthvisits.Clinic
import ca.bc.gov.data.datasource.local.entity.healthvisits.HealthVisitEntity
import ca.bc.gov.data.datasource.local.entity.hospitalvisit.HospitalVisitEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationForecastEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecommendationEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecordEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabTestEntity
import ca.bc.gov.data.datasource.local.entity.medication.DispensingPharmacyEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationRecordEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationSummaryEntity
import ca.bc.gov.data.datasource.local.entity.notification.NotificationEntity
import ca.bc.gov.data.datasource.local.entity.services.DiagnosticImagingDataEntity
import ca.bc.gov.data.datasource.local.entity.services.OrganDonorEntity
import ca.bc.gov.data.datasource.local.entity.settings.AppFeatureEntity
import ca.bc.gov.data.datasource.local.entity.settings.QuickAccessTileEntity
import ca.bc.gov.data.datasource.local.entity.specialauthority.SpecialAuthorityEntity
import ca.bc.gov.data.datasource.local.entity.userprofile.UserProfileEntity

fun PatientDto.toEntity() = PatientEntity(
    id = id,
    fullName = fullName,
    firstName = firstName,
    lastName = lastName,
    legalName = legalName?.toEntity(),
    commonName = commonName?.toEntity(),
    preferredName = preferredName?.toEntity(),
    dateOfBirth = dateOfBirth,
    phn = phn,
    patientOrder = Long.MAX_VALUE,
    authenticationStatus = authenticationStatus,
    mailingAddress = mailingAddress?.toEntity(),
    physicalAddress = physicalAddress?.toEntity(),
)

fun PatientNameDto.toEntity() = PatientNameEntity(
    givenName, surName
)

fun PatientAddressDto.toEntity() = PatientAddressEntity(
    streetLines = streetLines,
    city = city,
    province = province,
    postalCode = postalCode,
)

fun UserProfileDto.toEntity() = UserProfileEntity(
    patientId = patientId,
    acceptedTermsOfService = acceptedTermsOfService,
    email = email,
    isEmailVerified = isEmailVerified,
    smsNumber = smsNumber,
    isPhoneVerified = isPhoneVerified
)

fun VaccineDoseDto.toEntity() = VaccineDoseEntity(
    vaccineRecordId = vaccineRecordId,
    productName = productName,
    providerName = providerName,
    lotNumber = lotNumber,
    date = date
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
    orderStatus,
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
    updatedBy,
    syncStatus
)

fun CovidOrderDto.toEntity() = CovidOrderEntity(
    covidOrderId = covidOrderId,
    patientId = patientId,
    phn = phn,
    orderingProviderIds = orderingProviderIds,
    orderingProviders = orderingProviders,
    reportingLab = reportingLab,
    location = location,
    ormOrOru = ormOrOru,
    messageDateTime = messageDateTime,
    messageId = messageId,
    additionalData = additionalData,
    reportAvailable = reportAvailable
)

fun CovidTestDto.toEntity(orderId: Long) = CovidTestEntity(
    id,
    orderId,
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
    status?.text,
    displayName,
    eligibleDate,
    dueDate
)

fun HealthVisitsDto.toEntity() = HealthVisitEntity(
    healthVisitId,
    patientId,
    id,
    encounterDate,
    specialtyDescription,
    practitionerName,
    Clinic(clinicDto?.name),
    dataSource = dataSource
)

fun HospitalVisitDto.toEntity() = HospitalVisitEntity(
    hospitalVisitId = id,
    patientId = patientId,
    healthService = healthService,
    location = location,
    provider = provider,
    visitType = visitType,
    visitDate = visitDate,
    dischargeDate = dischargeDate,
)

fun SpecialAuthorityDto.toEntity() = SpecialAuthorityEntity(
    specialAuthorityId,
    patientId,
    referenceNumber,
    drugName,
    requestStatus,
    prescriberFirstName,
    prescriberLastName,
    requestedDate,
    effectiveDate,
    expiryDate,
    dataSource
)

fun ImmunizationRecommendationsDto.toEntity() = ImmunizationRecommendationEntity(
    recommendationSetId = recommendationSetId,
    patientId = patientId,
    immunizationName = immunizationName,
    status = status?.text,
    agentDueDate = agentDueDate,
    recommendedVaccinations = recommendedVaccinations
)

fun DependentDto.toEntity(patientId: Long, guardianId: Long) = DependentEntity(
    hdid = hdid,
    firstname = firstname,
    lastname = lastname,
    phn = phn,
    dateOfBirth = dateOfBirth,
    gender = gender,
    patientId = patientId,
    guardianId = guardianId,
    ownerId = ownerId,
    delegateId = delegateId,
    reasonCode = reasonCode,
    totalDelegateCount = totalDelegateCount,
    version = version,
    isCacheValid = isCacheValid
)

/**
 * [PatientEntity.legalName], [PatientEntity.preferredName] and [PatientEntity.commonName]
 * is set to [null] as these parameters are only expected in patient v2 api.
 * [DependentDto] is still on v1 and not supporting new naming fields.
 * TODO: do appropriate handling with v2 api for legal, preferred & common name
 */
fun DependentDto.toPatientEntity() = PatientEntity(
    fullName = getFullName(),
    dateOfBirth = dateOfBirth,
    phn = phn,
    patientOrder = Long.MAX_VALUE,
    authenticationStatus = AuthenticationStatus.DEPENDENT,
    physicalAddress = null,
    mailingAddress = null,
    firstName = firstname,
    lastName = lastname,
    legalName = null,
    commonName = null,
    preferredName = null
)

fun ClinicalDocumentDto.toEntity() = ClinicalDocumentEntity(
    patientId = patientId,
    fileId = fileId,
    name = name,
    type = type,
    facilityName = facilityName,
    discipline = discipline,
    serviceDate = serviceDate,
)

fun OrganDonorDto.toEntity() = OrganDonorEntity(
    id = id,
    patientId = patientId,
    status = status,
    statusMessage = statusMessage,
    registrationFileId = registrationFileId,
    file = file
)

fun DiagnosticImagingDataDto.toEntity() = DiagnosticImagingDataEntity(
    id = _id,
    patientId = patientId,
    diagnosticImagingId = id,
    examDate = examDate,
    fileId = fileId,
    examStatus = examStatus,
    healthAuthority = healthAuthority,
    organization = organization,
    modality = modality,
    bodyPart = bodyPart,
    procedureDescription = procedureDescription
)

fun NotificationDto.toEntity() = NotificationEntity(
    notificationId = notificationId,
    hdid = hdid,
    category = category,
    displayText = displayText,
    actionUrl = actionUrl,
    actionType = actionType.value,
    date = date,
)

fun AppFeatureDto.toEntity() = AppFeatureEntity(
    id,
    featureNameId,
    featureIconId,
    destinationId,
    isManagementEnabled,
    isQuickAccessEnabled
)

fun QuickAccessTileDto.toEntity() = QuickAccessTileEntity(
    id,
    featureId,
    titleNameId,
    titleIconId,
    isEnabled
)
