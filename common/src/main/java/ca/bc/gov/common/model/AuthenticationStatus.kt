package ca.bc.gov.common.model

enum class AuthenticationStatus(val source: Int) {
    AUTHENTICATED(1),
    NON_AUTHENTICATED(2),
    AUTHENTICATION_EXPIRED(3)
}