package ca.bc.gov.data.model.mapper

import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.PatientAddressDto
import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.model.healthvisits.ClinicDto
import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.common.model.immunization.ForecastStatus
import ca.bc.gov.common.model.immunization.ImmunizationForecastDto
import ca.bc.gov.common.model.immunization.ImmunizationRecommendationsDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastAndPatientDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastDto
import ca.bc.gov.common.model.labtest.LabOrderDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestsAndPatientDto
import ca.bc.gov.common.model.labtest.LabTestDto
import ca.bc.gov.common.model.notification.NotificationActionTypeDto
import ca.bc.gov.common.model.notification.NotificationDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.patient.PatientListDto
import ca.bc.gov.common.model.patient.PatientNameDto
import ca.bc.gov.common.model.patient.PatientWithClinicalDocumentsDto
import ca.bc.gov.common.model.patient.PatientWithCovidOrderAndTestDto
import ca.bc.gov.common.model.patient.PatientWithDataDto
import ca.bc.gov.common.model.patient.PatientWithHealthVisitsDto
import ca.bc.gov.common.model.patient.PatientWithHospitalVisitsDto
import ca.bc.gov.common.model.patient.PatientWithImmunizationRecordAndForecastDto
import ca.bc.gov.common.model.patient.PatientWithLabOrderAndLatTestsDto
import ca.bc.gov.common.model.patient.PatientWithSpecialAuthorityDto
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.common.model.relation.PatientWithMedicationRecordDto
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.common.model.relation.VaccineWithDosesDto
import ca.bc.gov.common.model.services.DiagnosticImagingDataDto
import ca.bc.gov.common.model.services.OrganDonorDto
import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.common.model.test.CovidOrderDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestAndPatientDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.common.model.userprofile.UserProfileDto
import ca.bc.gov.common.utils.titleCase
import ca.bc.gov.data.datasource.local.entity.PatientAddressEntity
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.PatientNameEntity
import ca.bc.gov.data.datasource.local.entity.clinicaldocument.ClinicalDocumentEntity
import ca.bc.gov.data.datasource.local.entity.comment.CommentEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderWithCovidTests
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderWithCovidTestsAndPatient
import ca.bc.gov.data.datasource.local.entity.covid.CovidTestEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineDoseEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineRecordEntity
import ca.bc.gov.data.datasource.local.entity.dependent.DependentEntity
import ca.bc.gov.data.datasource.local.entity.healthvisits.HealthVisitEntity
import ca.bc.gov.data.datasource.local.entity.hospitalvisit.HospitalVisitEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationForecastEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecommendationEntity
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
import ca.bc.gov.data.datasource.local.entity.notification.NotificationEntity
import ca.bc.gov.data.datasource.local.entity.relations.MedicationWithSummaryAndPharmacy
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithClinicalDocuments
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithCovidOrderAndCovidTest
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithData
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithHealthVisits
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithHospitalVisits
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithImmunizationRecordAndForecast
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithLabOrdersAndLabTests
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithMedicationRecords
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithSpecialAuthorities
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithVaccineAndDoses
import ca.bc.gov.data.datasource.local.entity.relations.VaccineRecordWithDose
import ca.bc.gov.data.datasource.local.entity.services.DiagnosticImagingDataEntity
import ca.bc.gov.data.datasource.local.entity.services.OrganDonorEntity
import ca.bc.gov.data.datasource.local.entity.specialauthority.SpecialAuthorityEntity
import ca.bc.gov.data.datasource.local.entity.userprofile.UserProfileEntity
import java.time.Instant

fun PatientEntity.toDto() = PatientDto(
    id = id,
    fullName = fullName.titleCase(),
    dateOfBirth = dateOfBirth,
    phn = phn,
    authenticationStatus = authenticationStatus,
    firstName = firstName,
    lastName = lastName,
    legalName = legalName?.toDto(),
    commonName = commonName?.toDto(),
    preferredName = preferredName?.toDto(),
    mailingAddress = mailingAddress?.toDto(),
    physicalAddress = physicalAddress?.toDto(),
)

fun PatientNameEntity.toDto() = PatientNameDto(
    givenName, surName
)

fun PatientAddressEntity.toDto() = PatientAddressDto(
    streetLines = streetLines,
    city = city,
    province = province,
    postalCode = postalCode,
)

fun UserProfileEntity.toDto() = UserProfileDto(
    patientId = patientId,
    acceptedTermsOfService = acceptedTermsOfService,
    email = email,
    isEmailVerified = isEmailVerified,
    smsNumber = smsNumber,
    isPhoneVerified = isPhoneVerified,
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

fun VaccineRecordWithDose.toDto() = VaccineWithDosesDto(
    vaccineRecordEntity.toDto(),
    doses = vaccineDoses.map { it.toDto() }
)

fun PatientWithVaccineAndDoses.toDto() = PatientWithVaccineAndDosesDto(
    patient = patient.toDto(),
    vaccineWithDoses = vaccineRecordWithDose?.toDto()
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

fun PatientWithHealthVisits.toDto() = PatientWithHealthVisitsDto(
    patient = patient.toDto(),
    healthVisits = healthVisits.map { it.toDto() }
)

fun PatientWithHospitalVisits.toDto() = PatientWithHospitalVisitsDto(
    patient = patient.toDto(),
    hospitalVisits = hospitalVisits.map { it.toDto() }
)

fun PatientWithClinicalDocuments.toDto() = PatientWithClinicalDocumentsDto(
    patient = patient.toDto(),
    clinicalDocuments = clinicalDocuments.map { it.toDto() }
)

fun PatientWithSpecialAuthorities.toDto() = PatientWithSpecialAuthorityDto(
    patient = patient.toDto(),
    specialAuthorities = specialAuthorities.map { it.toDto() }
)

fun PatientWithData.toDto() = PatientWithDataDto(
    patient = patient.toDto(),
    diagnosticImagingDataList = diagnosticImagingDataList.map { it.toDto() }
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
    orderStatus,
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
    updatedBy,
    syncStatus
)

fun CovidOrderEntity.toDto() = CovidOrderDto(
    id,
    covidOrderId,
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
    dataSource
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
    ForecastStatus.getByText(status),
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

fun HealthVisitEntity.toDto() = HealthVisitsDto(
    healthVisitId,
    patientId,
    id,
    encounterDate,
    specialtyDescription,
    practitionerName,
    ClinicDto(name = clinic?.name),
    dataSource = dataSource
)

fun HospitalVisitEntity.toDto() = HospitalVisitDto(
    id = hospitalVisitId,
    patientId = patientId,
    healthService = healthService,
    location = location,
    provider = provider,
    visitType = visitType,
    visitDate = visitDate,
    dischargeDate = dischargeDate,
)

fun ClinicalDocumentEntity.toDto() = ClinicalDocumentDto(
    id = clinicalDocumentId,
    patientId = patientId,
    name = name,
    type = type,
    facilityName = facilityName,
    discipline = discipline,
    serviceDate = serviceDate,
    fileId = fileId,
)

fun SpecialAuthorityEntity.toDto() = SpecialAuthorityDto(
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

fun ImmunizationRecommendationEntity.toDto() = ImmunizationRecommendationsDto(
    recommendationSetId = this.recommendationSetId,
    immunizationName = this.immunizationName,
    status = ForecastStatus.getByText(status),
    agentDueDate = this.agentDueDate,
    recommendedVaccinations = this.recommendedVaccinations
)

fun DependentEntity.toDto() = DependentDto(
    hdid = hdid,
    firstname = firstname,
    lastname = lastname,
    phn = phn,
    dateOfBirth = dateOfBirth,
    gender = gender,
    ownerId = ownerId,
    delegateId = delegateId,
    reasonCode = reasonCode,
    totalDelegateCount = totalDelegateCount,
    version = version,
    patientId = patientId,
    isCacheValid = isCacheValid,
)

fun OrganDonorEntity.toDto() = OrganDonorDto(
    id = id,
    patientId = patientId,
    status = status,
    statusMessage = statusMessage,
    registrationFileId = registrationFileId,
    file = file
)

fun DiagnosticImagingDataEntity.toDto() = DiagnosticImagingDataDto(
    _id = id,
    id = diagnosticImagingId,
    patientId = patientId,
    examDate = examDate,
    fileId = fileId,
    examStatus = examStatus,
    healthAuthority = healthAuthority,
    organization = organization,
    modality = modality,
    bodyPart = bodyPart,
    procedureDescription = procedureDescription
)

fun NotificationEntity.toDto() = NotificationDto(
    id = notificationId,
    hdid = hdid,
    category = category,
    displayText = displayText,
    actionUrl = actionUrl,
    actionType = NotificationActionTypeDto.getByValue(actionType),
    date = date,
)
