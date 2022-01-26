package ca.bc.gov.repository.model.mapper

import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.data.local.entity.PatientEntity

fun PatientEntity.toPatient() = PatientDto(
    id, firstName, lastName, dateOfBirth, phn
)
