package ca.bc.gov.bchealth.model.network.responses.vaccinestatus

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
* Created by amit_metri on 09,November,2021
*/
@Parcelize
data class FederalVaccineProof(
    val mediaType: String?,
    val encoding: String?,
    var data: String?
) : Parcelable
