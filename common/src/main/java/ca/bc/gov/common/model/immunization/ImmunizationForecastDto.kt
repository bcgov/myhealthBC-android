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

enum class ForecastStatus(val text: String) {
    UP_TO_DATE("Up to date"), // up to date in series
    ELIGIBLE("Elegible"), // eligible date has passed
    DUE("Due"), // within 2 weeks of due date
    OVERDUE("Overdue"); // Overdue: past due date

    companion object {
        fun getByText(text: String?) = values().firstOrNull {
            it.text == text
        }
    }
}
