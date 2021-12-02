package ca.bc.gov.bchealth.model.healthpasses.qr

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [Performer]
 *
 * @author Pinakin Kansara
 */
@Parcelize
data class Performer(
    val actor: Actor
) : Parcelable
