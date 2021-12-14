package ca.bc.gov.common.model.settings

/**
 * @author Pinakin Kansara
 */
enum class AnalyticsFeature(val value: Int) {
    ENABLED(1),
    DISABLED(2);

    companion object {
        fun getByValue(value: Int) = values().firstOrNull { it.value == value }
    }
}