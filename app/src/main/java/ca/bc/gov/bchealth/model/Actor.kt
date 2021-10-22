package ca.bc.gov.bchealth.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [Actor]
 *
 * @author Pinakin Kansara
 */
@Parcelize
data class Actor(
    val display: String
) : Parcelable
