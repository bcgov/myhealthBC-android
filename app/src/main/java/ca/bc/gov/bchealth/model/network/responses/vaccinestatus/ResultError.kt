package ca.bc.gov.bchealth.model.network.responses.vaccinestatus

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
* Created by amit_metri on 13,October,2021
*/
@Parcelize
data class ResultError(
    val resultMessage: String,
    val errorCode: String,
    val traceId: String,
    val actionCode: String
) : Parcelable
