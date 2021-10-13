package ca.bc.gov.bchealth.model.network.responses.vaccinestatus

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
    val totalResultCount: Int,
    val pageIndex: Int,
    val pageSize: Int,
    val resultStatus: Int,
    val resultError: ResultError
) : Parcelable
