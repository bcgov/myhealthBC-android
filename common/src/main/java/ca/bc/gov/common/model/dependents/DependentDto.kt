package ca.bc.gov.common.model.dependents

import java.time.Instant

data class DependentDto(
    val hdid: String,
    val firstname: String,
    val lastname: String,
    val phn: String,
    val dateOfBirth: Instant,
    val gender: String,
    val patientId: Long = 0,
    val isCacheValid: Boolean = false
) {
    fun getFullName() = "$firstname $lastname"
}
