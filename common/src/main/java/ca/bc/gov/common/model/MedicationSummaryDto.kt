package ca.bc.gov.common.model

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class MedicationSummaryDto(
    val id: Long = 0,
    var medicationRecordId: Long = -1,
    val din: String?,
    val brandName: String?,
    val genericName: String?,
    val quantity: Float,
    val maxDailyDosage: Float,
    val drugDiscontinueDate: Instant,
    val form: String?,
    val manufacturer: String?,
    val strength: String?,
    val strengthUnit: String?,
    val isPin: Boolean = false,
    val pharmacyAssessmentTitle: String?,
    val prescriptionProvided: Boolean = false,
    val redirectedToHealthCareProvider: Boolean = false,
    val title: String?,
    val subtitle: String?,
    val isPharmacistAssessment: Boolean = false
)
