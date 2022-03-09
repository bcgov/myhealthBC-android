package ca.bc.gov.bchealth.model.mapper

import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.healthpass.FederalTravelPassState
import ca.bc.gov.bchealth.ui.healthpass.HealthPass
import ca.bc.gov.bchealth.ui.healthpass.PassState
import ca.bc.gov.bchealth.ui.healthrecord.PatientHealthRecord
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordItem
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.ImmunizationStatus
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.patient.PatientWithHealthRecordCount
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsDto
import ca.bc.gov.common.model.relation.VaccineWithDosesDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toDateTimeString

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
        name = patient.fullName.uppercase(),
        qrIssuedDate = "Issued on ${
        vaccineWithDoses?.vaccine?.qrIssueDate
            ?.toDateTimeString()
        }",
        shcUri = vaccineWithDoses?.vaccine?.shcUri!!,
        qrCode = vaccineWithDoses?.vaccine?.qrCodeImage,
        state = passState,
        isExpanded = false,
        federalTravelPassState = federalTravelPassState,
        isAuthenticated = patient.authenticationStatus == AuthenticationStatus.AUTHENTICATED
    )
}

fun VaccineWithDosesDto.toUiModel(): HealthRecordItem {

    val passState = getHealthPassStateResources(vaccine.status)

    val date = doses.maxOf { it.date }
    return HealthRecordItem(
        patientId = vaccine.patientId,
        testResultId = -1L,
        medicationRecordId = -1L,
        icon = R.drawable.ic_health_record_vaccine,
        title = "COVID-19 vaccination",
        description = "${passState.status}",
        testOutcome = null,
        date = date.toDate(),
        HealthRecordType.VACCINE_RECORD,
        dataSource = vaccine.mode.name
    )
}

fun MedicationWithSummaryAndPharmacyDto.toUiModel(): HealthRecordItem {

    return HealthRecordItem(
        patientId = medicationRecord.patientId,
        testResultId = -1L,
        medicationRecordId = medicationRecord.id,
        title = medicationSummary.brandName ?: "",
        icon = R.drawable.ic_health_record_medication,
        description = medicationSummary.genericName ?: "",
        testOutcome = null,
        date = medicationRecord.dispenseDate.toDate(),
        healthRecordType = HealthRecordType.MEDICATION_RECORD,
        dataSource = medicationRecord.dataSource.name
    )
}

fun TestResultWithRecordsDto.toUiModel(): HealthRecordItem {

    val testRecordDto = testRecords.maxByOrNull { it.resultDateTime }
    val testOutcome = if (testRecordDto?.testStatus.equals("Pending", true)) {
        testRecordDto?.testStatus
    } else {
        when (testRecordDto?.testOutcome) {
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
    val date = testResult.collectionDate

    return HealthRecordItem(
        patientId = testResult.patientId,
        testResultId = testResult.id,
        medicationRecordId = -1L,
        icon = R.drawable.ic_health_record_covid_test,
        title = "COVID-19 test result",
        description = "",
        testOutcome = testOutcome,
        date = date.toDate(),
        HealthRecordType.COVID_TEST_RECORD,
        dataSource = testResult.dataSource.name
    )
}

fun LabOrderWithLabTestDto.toUiModel(): HealthRecordItem {
    var description = ""
    description = if (labTests.size > 1) {
        description.plus(labTests.size).plus(" tests").plus(" • ")
            .plus(labOrder.collectionDateTime.toDate())
    } else {
        description.plus(labTests.size).plus(" test").plus(" • ")
            .plus(labOrder.collectionDateTime.toDate())
    }
    return HealthRecordItem(
        patientId = labOrder.patientId,
        title = labOrder.commonName ?: "",
        labOrderId = labOrder.id,
        icon = R.drawable.ic_lab_test,
        date = labOrder.collectionDateTime.toDate(),
        description = description,
        testOutcome = null,
        healthRecordType = HealthRecordType.LAB_TEST
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

fun PatientWithHealthRecordCount.toUiModel(): PatientHealthRecord {
    return PatientHealthRecord(
        patientId = patientDto.id,
        name = patientDto.fullName,
        totalRecord = vaccineRecordCount + testResultCount + medicationRecordCount,
        authStatus = patientDto.authenticationStatus
    )
}

enum class CovidTestResultStatus {
    Negative,
    Positive,
    Indeterminate,
    IndeterminateResult,
    Cancelled,
    Pending
}
