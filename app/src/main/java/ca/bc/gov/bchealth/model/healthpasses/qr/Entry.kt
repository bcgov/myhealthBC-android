package ca.bc.gov.bchealth.model.healthpasses.qr

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [Entry]
 *
 * @author Pinakin Kansara
 */
@Parcelize
data class Entry(
    val fullUrl: String,
    val resource: Resource
) : Parcelable
