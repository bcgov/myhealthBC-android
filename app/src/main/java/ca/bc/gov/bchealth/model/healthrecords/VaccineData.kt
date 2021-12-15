package ca.bc.gov.bchealth.model.healthrecords

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

/*
* Created by amit_metri on 25,November,2021
*/
@Parcelize
data class VaccineData(
    var doseNumber: String? = "",
    val occurrenceDate: LocalDate?,
    val product: String? = "",
    val provider: String? = "",
    val lotNumber: String? = ""
) : Parcelable
