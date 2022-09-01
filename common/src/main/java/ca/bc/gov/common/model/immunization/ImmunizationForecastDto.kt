package ca.bc.gov.common.model.immunization

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class ImmunizationForecastDto(
    val id: Long = 0,
    var immunizationRecordId: Long = 0,
    val recommendationId: String? = null,
    val createDate: Instant,
    val status: ForecastStatus? = null,
    val displayName: String? = null,
    val eligibleDate: Instant,
    val dueDate: Instant
)

sealed class ForecastStatus(open val text: String) {
    object UpToDate : ForecastStatus("Up to date") // up to date in series
    object Eligible : ForecastStatus("Eligible") // eligible date has passed
    object Due : ForecastStatus("Due") // within 2 weeks of due date
    object Overdue : ForecastStatus("Overdue") // Overdue: past due date
    data class Other(override val text: String) : ForecastStatus(text)

    companion object {
        fun getByText(text: String?): ForecastStatus? = when (text) {
            UpToDate.text -> UpToDate
            Eligible.text -> Eligible
            Due.text -> Due
            Overdue.text -> Overdue
            null -> null
            else -> Other(text)
        }
    }
}
