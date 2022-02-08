package ca.bc.gov.data.remote.model.base.medication

data class MedicationSummary(
    val brandName: String?,
    val din: String?,
    val drugDiscontinuedDate: String?,
    val form: String?,
    val genericName: String?,
    val isPin: Boolean?,
    val manufacturer: String?,
    val maxDailyDosage: Int?,
    val quantity: Int?,
    val strength: String?,
    val strengthUnit: String?
)
