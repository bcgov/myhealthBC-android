package ca.bc.gov.bchealth.model.healthrecords

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
* Created by amit_metri on 25,November,2021
*/
@Parcelize
data class CovidTestResultData(
        val result: String
) : Parcelable
