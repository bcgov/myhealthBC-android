package ca.bc.gov.bchealth.model.mapper

import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.comment.Comment
import ca.bc.gov.bchealth.ui.dependents.DependentDetailItem
import ca.bc.gov.bchealth.ui.healthpass.FederalTravelPassState
import ca.bc.gov.bchealth.ui.healthpass.HealthPass
import ca.bc.gov.bchealth.ui.healthpass.PassState
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordItem
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordType
import ca.bc.gov.bchealth.ui.healthrecord.immunization.ForecastDetailItem
import ca.bc.gov.bchealth.ui.healthrecord.immunization.ImmunizationDoseDetailItem
import ca.bc.gov.bchealth.ui.healthrecord.immunization.ImmunizationRecordDetailItem
import ca.bc.gov.bchealth.ui.recommendations.RecommendationDetailItem
import ca.bc.gov.bchealth.utils.orPlaceholder
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.ImmunizationStatus
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.common.model.immunization.ImmunizationForecastDto
import ca.bc.gov.common.model.immunization.ImmunizationRecommendationsDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastAndPatientDto
import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.patient.PatientWithDataDto
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.common.model.services.BcCancerScreeningDataDto
import ca.bc.gov.common.model.services.DiagnosticImagingDataDto
import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toDateTimeString
import ca.bc.gov.common.utils.toLocalDateTimeInstant
import java.time.Instant
import java.time.LocalDate

fun PatientWithVaccineAndDosesDto.toUiModel(): HealthPass {

    val passState = getHealthPassStateResources(vaccineWithDoses?.vaccine?.status)

    val federalTravelPassState = if (vaccineWithDoses?.vaccine?.federalPass.isNullOrBlank()) {
        FederalTravelPassState(
            title = R.string.get_federal_proof_of_vaccination,
            icon = R.drawable.ic_federal_pass_add,
            null
        )
    } else {
        FederalTravelPassState(
            title = R.string.show_federal_proof_of_vaccination,
            icon = R.drawable.ic_federal_pass_forward_arrow,
            vaccineWithDoses?.vaccine?.federalPass
        )
    }

    return HealthPass(
        patientId = patient.id,
        vaccineRecordId = vaccineWithDoses?.vaccine?.id!!,
        name = patient.fullName,
        qrIssuedDate = "Issued on ${
        vaccineWithDoses?.vaccine?.qrIssueDate
            ?.toDateTimeString()
        }",
        shcUri = vaccineWithDoses?.vaccine?.shcUri!!,
        qrCode = vaccineWithDoses?.vaccine?.qrCodeImage,
        state = passState,
        isExpanded = false,
        federalTravelPassState = federalTravelPassState,
        isRemovable = with(patient.authenticationStatus) {
            this != AuthenticationStatus.AUTHENTICATED && this != AuthenticationStatus.DEPENDENT
        }
    )
}

fun MedicationWithSummaryAndPharmacyDto.toUiModel() = HealthRecordItem(
    patientId = medicationRecord.patientId,
    recordId = medicationRecord.id,
    title = medicationSummary.brandName ?: "",
    icon = R.drawable.ic_health_record_medication,
    description = medicationSummary.genericName.orEmpty() + " • " + medicationRecord.dispenseDate.toDate(),
    date = medicationRecord.dispenseDate,
    healthRecordType = HealthRecordType.MEDICATION_RECORD,
    dataSource = medicationRecord.dataSource.name
)

fun ClinicalDocumentDto.toUiModel() =
    HealthRecordItem(
        patientId = patientId,
        recordId = id,
        title = name,
        description = type,
        icon = R.drawable.ic_health_record_clinical_document,
        date = serviceDate,
        healthRecordType = HealthRecordType.CLINICAL_DOCUMENT_RECORD,
        dataSource = null
    )

fun LabOrderWithLabTestDto.toUiModel(): HealthRecordItem {
    var description = ""
    description = mapOrderStatus(labOrder.orderStatus ?: "").plus(" • ")
        .plus(labOrder.timelineDateTime.toDate())
    return HealthRecordItem(
        patientId = labOrder.patientId,
        title = labOrder.commonName ?: "",
        recordId = labOrder.id,
        icon = R.drawable.ic_lab_test,
        date = labOrder.timelineDateTime,
        description = description,
        healthRecordType = HealthRecordType.LAB_RESULT_RECORD,
        dataSource = labOrder.dataSorce.name
    )
}

fun mapOrderStatus(orderStatus: String): String {
    return when {
        orderStatus.equals("Held", true) -> {
            "Pending"
        }
        orderStatus.equals("Pending", true) -> {
            "Pending"
        }
        orderStatus.equals("Partial", true) -> {
            "Pending"
        }
        orderStatus.equals("Completed", true) -> {
            "Completed"
        }
        orderStatus.equals("Cancelled", true) -> {
            "Cancelled"
        }
        else -> {
            orderStatus
        }
    }
}

fun CovidOrderWithCovidTestDto.toUiModel(): HealthRecordItem {

    val covidTestResult = covidTests.maxByOrNull { it.collectedDateTime }
    val testOutcome = if (covidTestResult?.testStatus.equals("Pending")) {
        covidTestResult?.testStatus
    } else {
        when (covidTestResult?.labResultOutcome) {
            CovidTestResultStatus.Indeterminate.name,
            CovidTestResultStatus.IndeterminateResult.name -> {
                CovidTestResultStatus.Indeterminate.name
            }
            CovidTestResultStatus.Cancelled.name -> {
                CovidTestResultStatus.Cancelled.name
            }
            CovidTestResultStatus.Negative.name -> {
                CovidTestResultStatus.Negative.name
            }
            CovidTestResultStatus.Positive.name -> {
                CovidTestResultStatus.Positive.name
            }
            else -> {
                CovidTestResultStatus.Indeterminate.name
            }
        }
    }
    val date = covidTestResult?.collectedDateTime ?: Instant.now()

    return HealthRecordItem(
        patientId = covidOrder.patientId,
        recordId = covidOrder.id,
        title = "COVID-19 test result",
        description = "$testOutcome • ${date.toDate()}",
        icon = R.drawable.ic_health_record_covid_test,
        date = date,
        healthRecordType = HealthRecordType.COVID_TEST_RECORD,
        dataSource = covidOrder.dataSource.name
    )
}

fun ImmunizationRecordWithForecastDto.toUiModel(): HealthRecordItem {

    return HealthRecordItem(
        patientId = immunizationRecord.patientId,
        recordId = immunizationRecord.id,
        title = immunizationRecord.immunizationName ?: "",
        description = immunizationRecord.dateOfImmunization.toDate(),
        icon = R.drawable.ic_health_record_vaccine,
        date = immunizationRecord.dateOfImmunization,
        healthRecordType = HealthRecordType.IMMUNIZATION_RECORD,
        dataSource = immunizationRecord.dataSorce.name
    )
}

fun getHealthPassStateResources(state: ImmunizationStatus?): PassState = when (state) {
    ImmunizationStatus.FULLY_IMMUNIZED -> {
        PassState(R.color.status_green, R.string.vaccinated, R.drawable.ic_check_mark)
    }
    ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
        PassState(R.color.blue, R.string.partially_vaccinated, 0)
    }
    else -> {
        PassState(R.color.grey, R.string.no_record, 0)
    }
}

fun ImmunizationRecordWithForecastAndPatientDto.toUiModel(): ImmunizationRecordDetailItem {

    return ImmunizationRecordDetailItem(
        id = immunizationRecordWithForecast.immunizationRecord.id,
        status = immunizationRecordWithForecast.immunizationRecord.status,
        dueDate = immunizationRecordWithForecast.immunizationForecast?.dueDate?.toDate(),
        name = immunizationRecordWithForecast.immunizationRecord.immunizationName,
        doseDetails = listOf(
            ImmunizationDoseDetailItem(
                id = immunizationRecordWithForecast.immunizationRecord.id,
                date = immunizationRecordWithForecast.immunizationRecord.dateOfImmunization.toDate(),
                productName = immunizationRecordWithForecast.immunizationRecord.productName,
                immunizingAgent = immunizationRecordWithForecast.immunizationRecord.agentName,
                providerOrClinicName = immunizationRecordWithForecast.immunizationRecord.provideOrClinic,
                lotNumber = immunizationRecordWithForecast.immunizationRecord.lotNumber,
                forecast = immunizationRecordWithForecast.immunizationForecast?.toUiModel()
            )
        )
    )
}

fun HealthVisitsDto.toUiModel() =
    HealthRecordItem(
        patientId = patientId,
        recordId = healthVisitId,
        title = specialtyDescription.orEmpty(),
        description = practitionerName.orEmpty() + " • " + encounterDate.toDate(),
        icon = R.drawable.ic_health_record_health_visit,
        date = encounterDate,
        healthRecordType = HealthRecordType.HEALTH_VISIT_RECORD,
        dataSource = dataSource.name
    )

fun SpecialAuthorityDto.toUiModel() = HealthRecordItem(
    patientId = patientId,
    recordId = specialAuthorityId,
    title = drugName.orEmpty(),
    description = requestStatus.orEmpty() + " • " + requestedDate?.toDate(),
    icon = R.drawable.ic_health_record_special_authority,
    date = requestedDate!!,
    healthRecordType = HealthRecordType.SPECIAL_AUTHORITY_RECORD,
    dataSource = dataSource.name
)

fun HospitalVisitDto.toUiModel() =
    HealthRecordItem(
        patientId = patientId,
        recordId = id,
        icon = R.drawable.ic_health_record_hospital_visit,
        title = location,
        description = visitType,
        date = visitDate,
        healthRecordType = HealthRecordType.HOSPITAL_VISITS_RECORD,
        dataSource = null
    )

fun ImmunizationRecommendationsDto.toUiModel() = RecommendationDetailItem(
    title = this.recommendedVaccinations.orPlaceholder(),
    status = this.status,
    date = this.agentDueDate?.toDate().orPlaceholder(),
)

fun DependentDto.toUiModel(currentDate: LocalDate) = DependentDetailItem(
    patientId = patientId,
    hdid = hdid,
    firstName = firstname,
    fullName = "$firstname $lastname",
    agedOut = isDependentAgedOut(currentDate)
)

fun CommentDto.toUiModel() = Comment(
    id,
    text,
    createdDateTime.toLocalDateTimeInstant(),
    version,
    entryTypeCode.orEmpty(),
    createdDateTime,
    createdBy.orEmpty(),
    updatedDateTime,
    updatedBy.orEmpty(),
    syncStatus
)

private fun ImmunizationForecastDto.toUiModel() = ForecastDetailItem(
    name = this.displayName.orPlaceholder(),
    status = this.status,
    date = this.dueDate.toDate(),
)

enum class CovidTestResultStatus {
    Negative,
    Positive,
    Indeterminate,
    IndeterminateResult,
    Cancelled,
    Pending
}

private fun DiagnosticImagingDataDto.toUiModel() = HealthRecordItem(
    recordId = _id,
    patientId = patientId,
    icon = R.drawable.ic_health_record_diagnostic_imaging,
    title = modality.orEmpty(),
    description = if (isUpdated) { "Updated" } else { examStatus } + " • " + examDate?.toDate(),
    date = examDate!!,
    healthRecordType = HealthRecordType.DIAGNOSTIC_IMAGING,
    dataSource = null
)

fun PatientWithDataDto.toUiModel(): List<HealthRecordItem> {
    return diagnosticImagingDataList.map { it.toUiModel() }
}

fun BcCancerScreeningDataDto.toUiModel() = HealthRecordItem(
    recordId = _id,
    patientId = patientId,
    icon = R.drawable.ic_health_record_bc_cancer_screening,
    title = type.value,
    description = programName + " • " + resultDateTime?.toDate(),
    date = eventDateTime!!,
    healthRecordType = HealthRecordType.BC_CANCER_SCREENING,
    dataSource = null
)
