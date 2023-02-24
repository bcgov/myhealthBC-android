package ca.bc.gov.common.model.userprofile

data class UserProfileDto(
    var patientId: Long = -1,
    val acceptedTermsOfService: Boolean,
    val email: String? = null,
    val isEmailVerified: Boolean,
    val smsNumber: String? = null,
)
