package ca.bc.gov.common.model.dependents

import java.time.Instant

data class DependentDto(
    val hdid: String,
    val firstname: String,
    val lastname: String,
    val PHN: String,
    val dateOfBirth: Instant,
    val gender: String,
)
