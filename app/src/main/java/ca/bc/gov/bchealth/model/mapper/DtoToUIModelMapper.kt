package ca.bc.gov.bchealth.model.mapper

import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.healthpass.FederalTravelPassState
import ca.bc.gov.bchealth.ui.healthpass.HealthPass
import ca.bc.gov.bchealth.ui.healthpass.PassState
import ca.bc.gov.bchealth.ui.healthrecord.PatientHealthRecord
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordItem
import ca.bc.gov.common.model.ImmunizationStatus
import ca.bc.gov.common.model.VaccineRecord
import ca.bc.gov.common.model.patient.PatientWithHealthRecordCount
import ca.bc.gov.common.model.test.TestResult
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toDateTimeString
import ca.bc.gov.repository.model.PatientVaccineRecord

fun PatientVaccineRecord.toUiModel(): HealthPass {

    val passState = getHealthPassStateResources(vaccineRecord.status)

    val federalTravelPassState = if (vaccineRecord.federalPass.isNullOrBlank()) {
        FederalTravelPassState(
            title = R.string.get_federal_proof_of_vaccination,
            icon = R.drawable.ic_federal_pass_add,
            null
        )
    } else {
        FederalTravelPassState(
            title = R.string.show_federal_proof_of_vaccination,
            icon = R.drawable.ic_federal_pass_forward_arrow,
            vaccineRecord.federalPass
        )
    }

    return HealthPass(
        patientId = patient.id,
        vaccineRecordId = vaccineRecord.id,
        name = "${patient.firstName} ${patient.lastName}",
        qrIssuedDate = "Issued on ${vaccineRecord.qrIssueDate.toDateTimeString()}",
        shcUri = vaccineRecord.shcUri,
        qrCode = vaccineRecord.qrCodeImage,
        state = passState,
        isExpanded = false,
        federalTravelPassState = federalTravelPassState
    )
}

fun VaccineRecord.toUiModel(): HealthRecordItem {

    val passState = getHealthPassStateResources(status)

    return HealthRecordItem(
        patientId = patientId,
        testResultId = -1L,
        icon = R.drawable.ic_health_record_vaccine,
        title = R.string.covid_19_vaccination,
        description = passState.status,
        date = qrIssueDate.toDate()
    )
}

fun TestResult.toUiModel(): HealthRecordItem {

    return HealthRecordItem(
        patientId = patientId,
        testResultId = id,
        icon = R.drawable.ic_health_record_covid_test,
        title = R.string.covid_19_test_result,
        description = 0,
        date = collectionDate.toDate()
    )
}

fun getHealthPassStateResources(state: ImmunizationStatus): PassState = when (state) {
    ImmunizationStatus.FULLY_IMMUNIZED -> {
        PassState(R.color.status_green, R.string.vaccinated, R.drawable.ic_check_mark)
    }
    ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
        PassState(R.color.blue, R.string.partially_vaccinated, 0)
    }
    ImmunizationStatus.INVALID -> {
        PassState(R.color.grey, R.string.no_record, 0)
    }
}

fun PatientWithHealthRecordCount.toUiModel(): PatientHealthRecord {

    val firstName =
        patient.firstName.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    val lastName =
        patient.lastName.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    return PatientHealthRecord(
        patientId = patient.id,
        name = "$firstName $lastName",
        totalRecord = vaccineRecordCount + testResultCount
    )
}