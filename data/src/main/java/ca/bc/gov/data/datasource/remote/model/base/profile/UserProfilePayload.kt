package ca.bc.gov.data.datasource.remote.model.base.profile

/**
 * @author Pinakin Kansara
 */
data class UserProfilePayload(
    val hdId: String,
    val acceptedTermsOfService: Boolean,
    val email: String? = null,
    val isEmailVerified: Boolean,
    val smsNumber: String? = null,
    val isSMSNumberVerified: Boolean,
    val hasTermsOfServiceUpdated: Boolean,
    val lastLoginDateTime: String? = null,
    val lastLoginDateTimes: List<String> = emptyList(),
    val closedDateTime: String? = null,
    val preferences: UserPreferences?
)
