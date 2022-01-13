package ca.bc.gov.common.model.test

import android.os.Parcelable
import java.time.Instant
import kotlinx.parcelize.Parcelize

/**
 * @author Pinakin Kansara
 */

@Parcelize
data class TestRecord(
    var id: String,
    var testResultId: Long = 0,
    val labName: String,
    val collectionDateTime: Instant,
    val resultDateTime: Instant,
    val testName: String,
    val testType: String?,
    val testOutcome: String,
    val testStatus: String,
    val resultTitle: String,
    val resultDescription: List<String>,
    val resultLink: String
) : Parcelable
