package ca.bc.gov.bchealth.model.network.responses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [VaxStatusResponse]
 *
 * @author amit metri
 */
@Parcelize
data class VaxStatusResponse(
    val resourcePayload: ResourcePayload,
    val totalResultCount: String,
    val pageIndex: String,
    val pageSize: String,
    val resultStatus: Int,
    val resultError: String
) : Parcelable
