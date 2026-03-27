package ca.bc.gov.common.model

enum class AppFeatureName(val value: String) {
    HEALTH_RECORDS("Health records"),
    IMMUNIZATION_SCHEDULES("Immunization schedules"),
    RECOMMENDED_IMMUNIZATIONS("Recommended immunizations"),
    HEALTH_RESOURCES("Health resources"),
    SERVICES("Services");

    companion object {
        private val map = AppFeatureName.values().associateBy(AppFeatureName::value)
        operator fun get(value: String) = map[value]
    }
}
