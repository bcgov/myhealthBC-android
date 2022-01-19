package ca.bc.gov.common.model

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class CreatePatientDto(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Instant,
    val phn: String? = null,
    val patientOrder : Long
)

