package ca.bc.gov.common.model.immunization

sealed class ForecastStatus(open val text: String) {
    object UpToDate : ForecastStatus("Up to date") // up to date in series
    object Eligible : ForecastStatus("Eligible") // eligible date has passed
    object Due : ForecastStatus("Due") // within 2 weeks of due date
    object Overdue : ForecastStatus("Overdue") // Overdue: past due date
    object Completed : ForecastStatus("Completed")
    data class Other(override val text: String) : ForecastStatus(text)

    companion object {
        fun getByText(text: String?): ForecastStatus? = when (text?.lowercase()) {
            UpToDate.text.lowercase() -> UpToDate
            Eligible.text.lowercase() -> Eligible
            Due.text.lowercase() -> Due
            Overdue.text.lowercase() -> Overdue
            Completed.text.lowercase() -> Completed
            null -> null
            else -> Other(text)
        }
    }
}