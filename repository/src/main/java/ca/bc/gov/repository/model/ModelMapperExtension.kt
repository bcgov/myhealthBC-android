package ca.bc.gov.repository.model

import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.utils.toDateTime
import ca.bc.gov.data.remote.model.base.VaccineResourcePayload

fun VaccineResourcePayload.toPatient() = PatientDto(
    id = 0,
    firstName,
    lastName,
    dateOfBirth = birthDate.toDateTime(),
    phn
)
