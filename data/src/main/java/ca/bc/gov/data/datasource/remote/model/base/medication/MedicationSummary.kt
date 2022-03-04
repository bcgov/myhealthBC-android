package ca.bc.gov.data.datasource.remote.model.base.medication

data class MedicationSummary(
    val brandName: String?,
    val din: String?,
    val drugDiscontinuedDate: String?,
    val form: String?,
    val genericName: String?,
    val isPin: Boolean?,
    val manufacturer: String?,
    val maxDailyDosage: Float,
    val quantity: Float,
    val strength: String?,
    val strengthUnit: String?
)
