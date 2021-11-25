package ca.bc.gov.bchealth.model.healthrecords

import android.os.Parcelable
import ca.bc.gov.bchealth.model.ImmunizationStatus
import kotlinx.parcelize.Parcelize

/*
* Created by amit_metri on 25,November,2021
*/
@Parcelize
data class HealthRecord(
        var name: String,
        val immunizationStatus: ImmunizationStatus,
        val issueDate: String = "",
        val vaccineDataList: List<VaccineData?>,
        val covidTestResultDataList: List<CovidTestResultData>
) : Parcelable
