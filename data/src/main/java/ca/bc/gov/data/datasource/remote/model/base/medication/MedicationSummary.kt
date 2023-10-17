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
    val strengthUnit: String?,
    val pharmacyAssessmentTitle: String?,
    val prescriptionProvided: Boolean?,
    val redirectedToHealthCareProvider: Boolean?,
    val title: String?,
    val subtitle: String?,
    val isPharmacistAssessment: Boolean?
)
