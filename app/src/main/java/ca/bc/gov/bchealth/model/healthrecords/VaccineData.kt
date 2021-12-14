package ca.bc.gov.bchealth.model.healthrecords

import android.os.Parcelable
import java.time.LocalDate
import kotlinx.parcelize.Parcelize

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
