package ca.bc.gov.common.model.test

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

/**
 * @author Pinakin Kansara
 */

@Parcelize
data class TestRecordDto(
    var id: String,
    var testResultId: Long = 0,
    var labName: String,
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
