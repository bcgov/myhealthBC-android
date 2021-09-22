package ca.bc.gov.bchealth.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [CredentialSubject]
 *
 * @author Pinakin Kansara
 */
@Parcelize
data class CredentialSubject(
    val fhirVersion: String,
    val fhirBundle: FhirBundle
) : Parcelable
