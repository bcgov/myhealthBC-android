package ca.bc.gov.common.model

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class MedicationSummaryDto(
    val id: Long,
    val medicationRecordId: Long,
    val din: Long,
    val brandName: String,
    val genericName: String,
    val quantity: Int,
    val maxDailyDosage: Int,
    val drugDiscontinueDate: Instant,
    val form: String,
    val manufacturer: String,
    val strength: String,
    val strengthUnit: String,
    val isPin: Boolean = false
)
