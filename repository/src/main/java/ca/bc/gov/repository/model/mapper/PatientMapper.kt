package ca.bc.gov.repository.model.mapper

import ca.bc.gov.common.model.CreatePatientDto
import ca.bc.gov.common.model.patient.Patient
import ca.bc.gov.data.local.entity.PatientEntity

fun Patient.toCreatePatientDto() = CreatePatientDto(
    firstName,
    lastName,
    dateOfBirth,
    phn
)

fun PatientEntity.toPatient() = Patient(
    id, firstName, lastName, dateOfBirth, phn
)