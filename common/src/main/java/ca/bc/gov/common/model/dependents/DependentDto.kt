package ca.bc.gov.common.model.dependents

import ca.bc.gov.common.utils.toLocalDate
import java.time.Instant
import java.time.LocalDate
import java.time.Period

private const val MAX_DEPENDENT_AGE = 12

data class DependentDto(
    val hdid: String,
    val firstname: String,
    val lastname: String,
    val phn: String,
    val dateOfBirth: Instant,
    val gender: String,
    val ownerId: String,
    val delegateId: String,
    val reasonCode: Long,
    val version: Long,
    val patientId: Long = 0,
    val isCacheValid: Boolean = false,
) {
    fun getFullName() = "$firstname $lastname"

    fun isDependentAgedOut(currentDate: LocalDate): Boolean {
        val birthDateLocal = dateOfBirth.toLocalDate()
        val years = Period.between(birthDateLocal, currentDate).years

        return years >= MAX_DEPENDENT_AGE
    }
}
