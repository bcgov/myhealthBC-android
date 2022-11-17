package ca.bc.gov.common.model

enum class AuthenticationStatus(val source: String) {
    AUTHENTICATED("AUTHENTICATED"),
    NON_AUTHENTICATED("NON_AUTHENTICATED"),
    AUTHENTICATION_EXPIRED("AUTHENTICATION_EXPIRED"),
    DEPENDENT("DEPENDENT"),
}
