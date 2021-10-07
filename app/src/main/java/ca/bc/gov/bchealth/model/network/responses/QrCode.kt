package ca.bc.gov.bchealth.model.network.responses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [QrCode]
 *
 * @author amit metri
 */
@Parcelize
data class QrCode(
    val mediaType: String,
    val encoding: String,
    val data: String
) : Parcelable
