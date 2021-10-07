package ca.bc.gov.bchealth.model.network.responses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [ResourcePayload]
 *
 * @author amit metri
 */
@Parcelize
data class ResourcePayload(
    val id: String,
    val loaded: Boolean,
    val retryin: Int,
    val personalhealthnumber: Int,
    val firstname: String,
    val lastname: String,
    val birthdate: String,
    val vaccinedate: String,
    val doses: Int,
    val state: Int,
    val qrCode: QrCode
) : Parcelable