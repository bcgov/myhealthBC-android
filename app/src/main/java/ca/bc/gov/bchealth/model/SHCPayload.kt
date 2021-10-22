package ca.bc.gov.bchealth.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [SHCPayload] holds SMART HEALTH CARD DATA.
 *
 * @author Pinakin Kansara
 */
@Parcelize
data class SHCPayload(
    val iss: String,
    val nbf: Long,
    val vc: Vc
) : Parcelable
