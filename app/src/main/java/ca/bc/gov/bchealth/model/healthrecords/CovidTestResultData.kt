package ca.bc.gov.bchealth.model.healthrecords

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
* Created by amit_metri on 25,November,2021
*/
@Parcelize
data class CovidTestResultData(
    val patientDisplayName: String,
    val lab: String,
    val reportId: String,
    val collectionDateTime: String,
    val resultDateTime: String,
    val testName: String,
    val testType: String,
    val testStatus: String,
    val testOutcome: String,
    val resultTitle: String,
    val resultDescription: String,
    val resultLink: String
) : Parcelable
