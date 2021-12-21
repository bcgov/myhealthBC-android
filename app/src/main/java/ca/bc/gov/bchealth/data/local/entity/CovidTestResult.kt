package ca.bc.gov.bchealth.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

/*
* Created by amit_metri on 26,November,2021
*/
@Parcelize
@Entity(tableName = "covid_test_results")
class CovidTestResult(

    /*
    * reportId is considered as Primary key to save each test result in a separate row.
    * Each member can have multiple tests done over a period of time.
    * */
    @PrimaryKey
    val reportId: String,
    var patientDisplayName: String,
    val lab: String,
    val collectionDateTime: LocalDateTime,
    val resultDateTime: LocalDateTime,
    val testName: String,
    val testType: String,
    val testStatus: String,
    val testOutcome: String,
    val resultTitle: String,
    val resultLink: String,
    val combinedReportId: String,
    val phn: String,
    val dob: String
) : Parcelable
