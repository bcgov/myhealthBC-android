package ca.bc.gov.repository.extensions

import ca.bc.gov.common.model.DataSource
import ca.bc.gov.common.model.ImmunizationStatus
import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.shcdecoder.model.SHCData
import ca.bc.gov.shcdecoder.model.VaccinationStatus
import ca.bc.gov.shcdecoder.model.getPatient
import java.time.Instant

private const val IMMUNIZATION = "Immunization"
private const val PATIENT = "Patient"
private const val CONDITION = "Condition"

fun SHCData.toPatient(): PatientDto {
    val patient = getPatient()
    return PatientDto(
        firstName = patient.firstName!!,
        lastName = patient.lastName!!,
        dateOfBirth = patient.dateOfBirth?.toDate()!!,
    )
}

fun VaccinationStatus.toImmunizationStatus(): ImmunizationStatus =
    when (this) {
        VaccinationStatus.FULLY_VACCINATED -> ImmunizationStatus.FULLY_IMMUNIZED
        VaccinationStatus.PARTIALLY_VACCINATED -> ImmunizationStatus.PARTIALLY_IMMUNIZED
        VaccinationStatus.INVALID -> ImmunizationStatus.INVALID
    }

fun SHCData.toPatientVaccineRecord(
    shcUri: String,
    status: VaccinationStatus
): PatientVaccineRecord {

    val entries = payload.vc.credentialSubject.fhirBundle.entry

    val doses = entries.filter { entry ->
        entry.resource.resourceType.contains(IMMUNIZATION)
    }.map { entry ->

        var provider: String? = null
        entry.resource.performer?.forEach {
            provider = it.actor.display
        }
        VaccineDoseDto(
            date = entry.resource.occurrenceDateTime?.toDate()!!,
            providerName = provider!!,
            productName = entry.resource.vaccineCode?.coding?.firstOrNull()?.code!!,
            lotNumber = " "
        )
    }

    val record = VaccineRecordDto(
        id = 0,
        patientId = 0,
        qrIssueDate = Instant.ofEpochSecond(payload.nbf.toLong()),
        doseDtos = doses,
        federalPass = null,
        shcUri = shcUri,
        status = status.toImmunizationStatus(),
        qrCodeImage = null,
        mode = DataSource.QR_CODE
    )

    return PatientVaccineRecord(toPatient(), record)
}
