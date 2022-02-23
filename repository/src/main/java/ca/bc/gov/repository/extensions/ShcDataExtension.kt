package ca.bc.gov.repository.extensions

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
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

fun SHCData.toPatientDto(): PatientDto {
    val patient = getPatient()
    val fullNameBuilder = StringBuilder()
    if (patient.firstName != null) {
        fullNameBuilder.append(patient.firstName)
        fullNameBuilder.append(" ")
    }
    if (patient.lastName != null) {
        fullNameBuilder.append(patient.lastName)
    }

    val dateOfBirth =
        patient.dateOfBirth ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
    return PatientDto(
        fullName = fullNameBuilder.toString(),
        dateOfBirth = dateOfBirth.toDate()
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
        val productCode = entry.resource.vaccineCode?.coding?.last()?.code
        val productName: String =
            vaccineInfo.getOrDefault(productCode, "UNSPECIFIED COVID-19 VACCINE")

        val occurrenceDateTime = entry.resource.occurrenceDateTime ?: throw MyHealthException(
            SERVER_ERROR,
            MESSAGE_INVALID_RESPONSE
        )

        VaccineDoseDto(
            date = occurrenceDateTime.toDate(),
            providerName = provider,
            productName = productName,
            lotNumber = entry.resource.lotNumber
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
        qrCodeImage = null
    )

    return PatientVaccineRecord(toPatientDto(), record)
}

private val vaccineInfo: HashMap<String, String> = mapOf(
    "28581000087106" to "PFIZER-BIONTECH COMIRNATY",
    "28571000087109" to "MODERNA SPIKEVAX",
    "28761000087108" to "ASTRAZENECA VAXZEVRIA",
    "28961000087105" to "COVISHIELD",
    "28951000087107" to "JANSSEN (JOHNSON & JOHNSON)",
    "29171000087106" to "NOVAVAX",
    "31431000087100" to "CANSINOBIO",
    "31341000087103" to "SPUTNIK",
    "31311000087104" to "SINOVAC-CORONAVAC ",
    "31301000087101" to "SINOPHARM",
    "NON-WHO" to "UNSPECIFIED COVID-19 VACCINE"
) as HashMap<String, String>
