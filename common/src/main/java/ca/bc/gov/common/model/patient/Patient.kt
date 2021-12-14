package ca.bc.gov.common.model.patient

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class Patient(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Instant,
    val phn: String? = null,
)
