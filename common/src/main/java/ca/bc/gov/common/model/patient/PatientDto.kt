package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.AuthenticationStatus
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class PatientDto(
    var id: Long = 0,
    val fullName: String,
    val dateOfBirth: Instant,
    var phn: String? = null,
    val patientOrder: Long = Long.MAX_VALUE,
    var authenticationStatus: AuthenticationStatus = AuthenticationStatus.NON_AUTHENTICATED
)
